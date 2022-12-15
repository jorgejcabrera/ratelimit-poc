from locust import HttpUser, task, between


class QuickstartUser(HttpUser):
    users = []

    def on_start(self):
        f = open("/mnt/locust/all_users", "r")
        allUsers = f.read()
        self.users = allUsers.split(",")

    @task(3)
    def hello_world(self):
        for token in self.users:
            self.client.post("", {"user_id": f'{token}', "message": "Hi! Jorge", "type": "status"})
