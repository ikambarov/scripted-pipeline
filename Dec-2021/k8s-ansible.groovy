podTemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    agent: ansible
  name: ansible
spec:
  containers:
  - image: ikambarov/ansible
    name: ansible
    args:
    - sleep
    - "100000"
'''

podTemplate(cloud: 'kubernetes', label: 'ansible', showRawYaml: false, yaml: podTemplate) {
    node('ansible'){
        container('ansible'){
            stage("Version"){
                sh "ansible --version"
            }
        }
    }
}