def podname="ansible"

def podtemplate="""
metadata:
  labels:
    run: $podname
spec:
  containers:
  - image: yourname/ansible
    name: $podname
    args:
    - sleep
    - "100000"
"""

podTemplate(name: podname, label: podname, yaml: podtemplate, showRawYaml: false) {
    node(podname){
        stage('Check version'){
            container(podname){
                sh '''
                    ansible --version
                '''
            }           
        }
    }
}
