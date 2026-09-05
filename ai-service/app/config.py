from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    APP_NAME: str = "CloudPilot AI Service"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False

    # LLM Settings (OpenAI, Gemini, or local fallback)
    OPENAI_API_KEY: Optional[str] = None
    LLM_MODEL: str = "gpt-4o-mini"
    LLM_TIMEOUT_SECONDS: float = 4.0

    # Embedding Model Settings
    EMBEDDING_MODEL_NAME: str = "all-MiniLM-L6-v2"
    USE_LIGHTWEIGHT_EMBEDDINGS: bool = True

    # CORS
    ALLOWED_ORIGINS: list[str] = ["http://localhost:3000", "http://localhost:5173", "http://localhost:8080", "*"]


settings = Settings()
