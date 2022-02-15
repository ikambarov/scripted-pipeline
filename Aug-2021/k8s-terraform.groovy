podyaml = '''
metadata:
  labels:
    run: terraform
  name: terraform
spec:
  containers:
  - args:
    - sleep
    - "1000000"
    image: ikambarov/terraform:0.14
    name: terraform
'''

podTemplate(cloud: 'kubernetes', label: 'terraform', name: 'terraform', namespace: 'jenkins', yaml: podyaml, showRawYaml: false) {
    node('terraform'){
        container('terraform'){
            stage("Test"){
                sh "terraform version"
            }   
        }
    }
}
