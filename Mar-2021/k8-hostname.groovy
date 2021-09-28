def podtemplate="""
metadata:
  labels:
    run: centos-pod
spec:
  containers:
  - image: centos
    name: centos-pod
    args:
    - sleep
    - "100000"
"""

podTemplate(name: 'centos-pod', label: 'k8-agent', yaml: podtemplate, showRawYaml: false) {
    node('k8-agent'){
        stage('Hostname'){
            sh '''
                hostname
            '''
        }
    }
}
