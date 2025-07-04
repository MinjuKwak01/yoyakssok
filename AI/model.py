from typing import List, Optional
from fastapi import APIRouter, Request, UploadFile, Header, HTTPException
from fastapi.responses import FileResponse
import os
import shutil
import json
from pydantic import BaseModel
import fitz  # PyMuPDF
import pymupdf4llm
from utils.token_validator import verify_token

from tempfile import NamedTemporaryFile

from openai import OpenAI
from dotenv import load_dotenv
import httpx
from langchain_core.prompts import PromptTemplate
import asyncio
from urllib.parse import unquote

upload = APIRouter(prefix='/ai')
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)
SUMMARY_DIR = "summaries"
os.makedirs(SUMMARY_DIR, exist_ok=True)

load_dotenv()

client = OpenAI (
    api_key = os.getenv("OPENAI_API_KEY")
)

# 경로 설정
@upload.post('/upload')
async def upload_file(
    file: UploadFile,
):

    with NamedTemporaryFile(delete=False, suffix=".pdf") as temp_file:
        shutil.copyfileobj(file.file, temp_file)
        temp_file_path = temp_file.name
    
    
    print(temp_file_path)
    loader = pymupdf4llm.to_markdown(temp_file_path)

    upload_file_path = os.path.join(UPLOAD_DIR, f"{file.filename}_summary.txt")
    with open(upload_file_path, "w") as upload_file:
        upload_file.write(loader)

    # 임시 파일 삭제
    os.remove(temp_file_path)

    return {"status":200, "message" : "업로드 완료", "data" : {"upload_file_path" : upload_file_path , "file_name" : file.filename}}


@upload.get("/summary")
async def summarize_file(
    upload_file_path: str,
    file_name: str,
    authorization: Optional[str] = Header(None, description="Bearer token")
):
    if not authorization or not authorization.startswith('Bearer '):
        return {"status":401, "message": "인증이 필요합니다", "data" : None}
    
    token = authorization.split(' ')[1]
    is_valid = await verify_token(token)

    if isinstance(is_valid, dict):
        return is_valid
    
    if not is_valid:
        return {"status":402, "message": "기본 이용을 전부 다 사용했습니다.", "data" : None}

    
    with open(upload_file_path, "r") as upload_file:
        summary_input = upload_file.read()

    summary_text = """input : {input} 
    \n 위 input은 학생들의 학습 자료로서 제공 된 내용입니다."""

    summary_context = """\n 
    학습 자료로서 복잡하고 전문적인 용어들이 자주 등장하며 용어의 중요도가 높은 편입니다.
    당신은 아주 중요한 시험을 준비하는 학생으로, input으로 제공된 학습 자료의 구체적이고 세부적인 내용까지 고려한 요약 레포트를 작성해야합니다.
    """

    summary_example = """
    또한 각각의 내용은 충분히 자세하고 세부적인 사항들을 포함하고 있어야 합니다.  
    문서의 끝부분까지 확실하게 설명해주세요.
    input의 내용에 없는 것을 추가하여 설명하지 마세요. 
    요약에 부가적인 설명을 붙이지 마세요. 오로지 요약만 전달해야합니다.

    \n 이 때 설명은 반드시 구체적인 내용을 작성해주세요.
    \n 예시로 '프로젝트 과제'에 대한 내용이 있다면 '프로젝트 과제 수행에는 레포트 작성, 유스케이스 작성 등이 있다'를 명시해주어야합니다.
    """
    summary_template = """
    반환 형식은 다음과 같습니다. 이 프로젝트에서 일관적인 반환 형식은 매우 중요한 요소입니다. 

    \n ### 1. 주제 1
    \n 주제 1에 대한 내용 설명
    ... 
    \n 주제의 개수는 input에 따라 달라질 수 있습니다. 

    """

    summary_prompt = (
        PromptTemplate.from_template(summary_text) 
        + PromptTemplate.from_template(summary_context) 
        + PromptTemplate.from_template(summary_example) 
        + summary_template
    )

    ## api 이용한 pdf 문서 요약
    summary_query = summary_prompt.format(input = summary_input)
    summary_message = [{'role': 'user', 'content': summary_query}]
    completion = client.chat.completions.create(model='gpt-3.5-turbo', messages= summary_message)
    summarized_input = completion.choices[0].message.content

    summary_file_path = os.path.join(SUMMARY_DIR, f"{file_name}_summary.txt")
    with open(summary_file_path, "w") as summarized_file:
        summarized_file.write(summarized_input)

    os.remove(upload_file_path)

    return {"status" : 200, "message" : "요약 완료", "data" : {"summarized_file_path" : summary_file_path , "file_name" : file_name, "summarized_input": summarized_input}}


@upload.get("/questions")
async def make_questions(
    summarized_file_path: str,
    file_name: str,
    authorization: Optional[str] = Header(None, description="Bearer token")
):
    # URL 디코딩
    summarized_file_path = unquote(summarized_file_path)
    file_name = unquote(file_name)
    
    # client 변수 정의
    client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
    
    if not authorization or not authorization.startswith('Bearer '):
        return {"status":401, "message": "인증이 필요합니다", "data" : None}
    
    token = authorization.split(' ')[1]
    is_valid = await verify_token(token)

    if isinstance(is_valid, dict):
        return is_valid
    
    if not is_valid:
        return {"status":402, "message": "기본 이용을 전부 다 사용했습니다.", "data": None}

    
    with open(summarized_file_path, "r") as summarized_file:
        summarized_input = summarized_file.read()

    quiz_data_schema = {
        "type": "object",
        "properties": {
            "questions": {
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "question": {
                            "type": "string",
                            "description": "input을 기반으로 하여 만든 쪽지 시험의 문제입니다."
                        },
                        "answer": {
                            "type": "string",
                            "description": "input을 기반으로 하여 만든 쪽지 시험의 정답입니다."
                        },

                    },
                    "required": ["question", "answer"],
                    "minProperties": 5,
                    "maxProperties": 5   
                }
            }
        },
        "required": ["questions"]
        
    }

    ## 질문 생성 프롬프트
    question_text = """input : {q_input} \n 
    위 input을 기반으로 쪽지시험 문제를 만들어야합니다.
    input은 학생들에게 제공된 학습 자료를 요약한 문서입니다.
    """

    question_context = """\n 
    위 input은 학생들의 학습 자료로서 제공된 내용으로, 학술적인 내용이 대부분 포함되어 있습니다.
    복잡하고 전문적인 용어들이 자주 등장하며 이런 용어의 중요도가 높은 편입니다.
    또한 당신은 매 수업마다 복습을 위한 쪽지시험 문제를 내는 교수자입니다. 
    학생들의 학습 이해도를 확인하기 위해서는 핵심적이고 정확한 문제와 답변을 생성해야합니다. 
    """

    question_query= """\n 위 input을 기반으로 하여 5개의 쪽지시험 문제를 만들어주세요.
    """

    question_prompt = (
        PromptTemplate.from_template(question_text) 
        + PromptTemplate.from_template(question_context) 

    )

    functions = [
        {
            "name": "generate_questions",
            "description": """입력받은 내용을 기반으로 하여 심도있는 쪽지시험 문제와 그 답을 만들어야합니다. 
            문제와 답안은 한국어로 출력해주세요.  

            답안은 존댓말을 사용하지 마세요.

            또한 item은 무조건 5개가 나와야 하며, 그 형식이 절대로 바뀌어서는 안됩니다.
            주어진 parameters 외에 다른 요소를 절대 추가하지 마세요.""",
            "parameters": quiz_data_schema
        }
    ]

    query = question_prompt.format(q_input = summarized_input) + question_query 
    message = [{'role': 'user', 'content': query}]
    questions = client.chat.completions.create(
        model='gpt-3.5-turbo',
        messages=message ,
        functions = functions,
        function_call = {"name" : "generate_questions"}
    )
    question = questions.choices[0].message.function_call.arguments
    question = json.loads(question)
  
    async with httpx.AsyncClient() as client:
        max_retries = 3
        retry_delay = 1 
        
        for attempt in range(max_retries):
            try:
                response = await client.post(
                    "https://fourever.duckdns.org/api/summary/complete",  
                    headers={"Authorization": f"Bearer {token}"},
                    json={"isComplete": True},
                    timeout=10.0
                )
                print(response.json().get("status"))
                if response.json().get("status") == 200:
                    break
                
                print(f"완료 상태 전송 실패 (시도 {attempt + 1}/{max_retries}): {response.json().get('message')}")
                if attempt < max_retries - 1:
                    await asyncio.sleep(retry_delay)
                    
            except httpx.RequestError as e:
                print(f"완료 상태 전송 중 오류 발생 (시도 {attempt + 1}/{max_retries}): {str(e)}")
                if attempt < max_retries - 1:
                    await asyncio.sleep(retry_delay)

    
    os.remove(summarized_file_path)
    return {"status" : 200, "message" : "문제 생성 완료", "data" : question}

@upload.post('/model')
async def start_model(authorization: Optional[str] = Header(None, description="Bearer token")):
    if not authorization or not authorization.startswith('Bearer '):
        raise HTTPException(
            status_code=401,
            detail="인증이 필요합니다"
        )
    
    token = authorization.split(' ')[1]
    is_valid = await verify_token(token)
    if not is_valid:
        return {"status" : 401, "message" : "기본 이용을 전부 다 사용했습니다.", "data" : None}

    return {'message': "model"}