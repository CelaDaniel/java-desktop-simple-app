import subprocess
import json

def get_container_id_by_image(image_name):
    try:
        output = subprocess.check_output(["docker", "ps", "--format", "{{.ID}} {{.Image}}"]).decode()
        for line in output.splitlines():
            container_id, container_image = line.split()
            if container_image == image_name:
                return container_id
    except subprocess.CalledProcessError as e:
        return f"An error occurred: {e}"
    return None

def get_container_ip(container_id):
    try:
        output = subprocess.check_output(["docker", "inspect", container_id]).decode()
        container_info = json.loads(output)
        ip_address = list(container_info[0]['NetworkSettings']['Networks'].values())[0]['IPAddress']
        return ip_address
    except subprocess.CalledProcessError as e:
        return f"An error occurred: {e}"
    except IndexError:
        return "IP Address not found."
    return None

# Replace 'dpage/pgadmin4' with the Docker image name you are interested in
container_id = get_container_id_by_image("postgres")
if container_id:
    ip_address = get_container_ip(container_id)
    if ip_address:
        print(f"IP Address: {ip_address}")
    else:
        print("Unable to retrieve IP Address.")
else:
    print("Container not found.")
