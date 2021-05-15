def k8name = "k8-docker"

def k8template = """
metadata:
  labels:
    agent: ${ k8name }
  name: ${ k8name }
spec:
  containers:
  - image: docker
    name: ${ k8name }
    command: 
    - sleep
    - 100000
    volumeMounts:
      - mountPath: /var/run/docker.sock
        name: docker-sock
  volumes:
    - name: docker-sock
      hostPath:
        path: /var/run/docker.sock
"""


podTemplate(name: k8name, label: k8name, yaml: k8template, showRawYaml: false){
    node(k8name){
        stage("Test"){
          container(k8name){
            sh "docker ps"
          }            
        }
    }
}