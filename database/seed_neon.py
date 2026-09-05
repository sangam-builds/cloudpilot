import psycopg2
import sys
import os

# Neon Connection details (configurable via environment variables)
DB_HOST = os.getenv("CLOUD_DB_HOST", "ep-twilight-tooth-ax4dzdsg-pooler.c-4.us-east-2.aws.neon.tech")
DB_NAME = os.getenv("CLOUD_DB_NAME", "neondb")
DB_USER = os.getenv("CLOUD_DB_USER", "neondb_owner")
DB_PASS = os.getenv("CLOUD_DB_PASSWORD", "npg_bvsK7ClNAhn2")
DB_PORT = int(os.getenv("CLOUD_DB_PORT", 5432))

def main():
    print(f"Connecting to Neon Cloud PostgreSQL ({DB_HOST})...")
    try:
        conn = psycopg2.connect(
            host=DB_HOST,
            dbname=DB_NAME,
            user=DB_USER,
            password=DB_PASS,
            port=DB_PORT,
            sslmode="require"
        )
        conn.autocommit = True
        cursor = conn.cursor()
        print("Connected successfully!\n")

        # 1. Execute Schema
        schema_path = os.path.join(os.path.dirname(__file__), "schema.sql")
        print(f"Applying schema from: {schema_path}")
        with open(schema_path, "r", encoding="utf-8") as f:
            schema_sql = f.read()
        cursor.execute(schema_sql)
        print("Schema applied successfully.\n")

        # 2. Execute Seed Data
        seed_path = os.path.join(os.path.dirname(__file__), "seed.sql")
        print(f"Applying seed data from: {seed_path}")
        with open(seed_path, "r", encoding="utf-8") as f:
            seed_sql = f.read()
        cursor.execute(seed_sql)
        print("Seed data applied successfully.\n")

        # 3. Verify row counts
        tables = ["customers", "orders", "teams", "agents", "tickets", "ticket_history", "faqs", "audit_logs"]
        print("================ SEED VERIFICATION ================")
        for table in tables:
            cursor.execute(f"SELECT COUNT(*) FROM {table};")
            count = cursor.fetchone()[0]
            print(f"  {table.ljust(18)} : {count} records")
        print("====================================================")
        print("\nAll tables seeded with production data in Neon PostgreSQL successfully!")

        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Error seeding database: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
