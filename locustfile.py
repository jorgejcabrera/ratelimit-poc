from locust import HttpUser, task, between


class QuickstartUser(HttpUser):
    users = []
    wait_time = between(5, 15)

    def on_start(self):
        f = open("/mnt/locust/all_users", "r")
        allUsers = f.read()
        self.users = allUsers.split(",")

    @task
    def hello_world(self):
        for token in self.users:
            self.client.headers = {"Content-type": "application/json"}
            self.client.post("", json={"user_id": f'{token}', "message": "Hi! Jorge", "type": "status"})
