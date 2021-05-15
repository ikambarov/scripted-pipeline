def k8name = "k8-tools"

def k8template = """
metadata:
  labels:
    agent: ${ k8name }
  name: ${ k8name }
spec:
  containers:
  - image: ikambarov/k8-tools
    name: k8-tools
  - image: docker
    name: k8-tools
"""

podTemplate(name: k8name, label: k8name, yaml: k8template, showRawYaml: false){
    node(k8name){
        stage("Build"){
            container("docker"){
                sh "docker build"
                sh "docker login"
                sh "docker push"
            }
        }
        stage("Deploy"){
            container("k8-tools"){
                sh "kubectl run install"
            }
        }
    }
}