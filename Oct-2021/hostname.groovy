pod = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: centos
  name: centos
spec:
  containers:
  - image: centos
    name: centos
    args:
    - sleep
    - "1000000"
'''

podTemplate(cloud: 'kubernetes', label: 'centos', name: 'centos', yaml: pod ) {
    node('centos'){
        stage("Hostname"){
            sh "hostname"
        }
    }
}
