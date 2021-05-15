def k8name = "k8-ansible"

def k8template = """
metadata:
  labels:
    agent: ${ k8name }
  name: ${ k8name }
spec:
  containers:
  - image: ikambarov/ansible
    name: k8-ansible
"""


podTemplate(name: k8name, label: k8name, yaml: k8template, showRawYaml: false){
    node(k8name){
        stage("Test"){
            container("k8-ansible"){
                sh "ansible version"
            }            
        }
    }
}