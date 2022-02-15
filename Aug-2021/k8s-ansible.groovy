podyaml = '''
metadata:
  labels:
    run: ansible
  name: ansible
spec:
  containers:
  - args:
    - sleep
    - "1000000"
    image: ikambarov/ansible:latest
    name: ansible
'''

podTemplate(cloud: 'kubernetes', label: 'ansible', name: 'ansible', namespace: 'jenkins', yaml: podyaml, showRawYaml: false) {
    node('ansible'){
        container('ansible'){
            stage("Test"){
                sh "ansible --version"
            }   
        }
    }
}
