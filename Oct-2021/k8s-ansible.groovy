pod = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: ansible
  name: ansible
spec:
  containers:
  - image: ikambarov/ansible
    name: ansible
    args:
    - sleep
    - "1000000"
'''

podTemplate(cloud: 'kubernetes', label: 'ansible', name: 'ansible', yaml: pod ) {
    node('ansible'){
        container('ansible'){
            stage("Check Ansible Version"){
                sh "ansible --version"
            }
        }
    }
}
