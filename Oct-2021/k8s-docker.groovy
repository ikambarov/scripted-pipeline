pod = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: docker
  name: docker
spec:
  containers:
  - image: docker
    name: docker
    args:
    - sleep
    - "1000000"
'''

podTemplate(cloud: 'kubernetes', label: 'docker', name: 'docker', yaml: pod ) {
    node('docker'){
        stage("Check docker Version"){
            container('docker'){
                sh "docker version"
            }
        }
    }
}
