def k8name = "k8-terraform"

def k8template = """
metadata:
  labels:
    agent: ${ k8name }
  name: ${ k8name }
spec:
  containers:
  - image: ikambarov/k8-tools
    name: k8-terraform
"""


podTemplate(name: k8name, label: k8name, yaml: k8template, showRawYaml: false){
    node(k8name){
        stage("Test"){
            container("k8-terraform"){
                sh "terraform version"
            }
        }
    }
}