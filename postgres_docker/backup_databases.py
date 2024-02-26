import subprocess
import os

# PostgreSQL connection settings
host = "localhost"  # or Docker container name
port = "5432"
user = "postgres"
password = "jetoni11@@"

# Backup directory within the current working directory
backup_dir = os.path.join(os.getcwd(), "Backup_DB")

def get_databases():
    """Fetches a list of databases from PostgreSQL."""
    try:
        cmd = [
            "docker", "exec", "-i", "postgres_container",
            "psql", "-U", user, "-d", "postgres", "-tAc", "SELECT datname FROM pg_database WHERE datistemplate = false;"
        ]
        result = subprocess.run(cmd, capture_output=True, text=True, env={"PGPASSWORD": password})

        if result.returncode != 0:
            print("Error executing command:", ' '.join(cmd))
            print("Error message:", result.stderr.strip())
            return []

        return [db for db in result.stdout.strip().split('\n') if db]

    except subprocess.CalledProcessError as e:
        print(f"An error occurred while fetching databases: {e}")
        return []

def export_database(db_name):
    file_name = os.path.join(backup_dir, f"{db_name}.sql")
    print(f"Backing up {db_name} to {file_name}")

    with open(file_name, "wb") as file:
        try:
            subprocess.run(
                ["docker", "exec", "-i", "postgres_container", "pg_dump", "-U", user, "-d", db_name],
                stdout=file, check=True, env={"PGPASSWORD": password}
            )
        except subprocess.CalledProcessError as e:
            print(f"An error occurred while exporting {db_name}: {e}")

if __name__ == "__main__":
    if not os.path.exists(backup_dir):
        os.makedirs(backup_dir)

    databases = get_databases()
    for db in databases:
        export_database(db)
