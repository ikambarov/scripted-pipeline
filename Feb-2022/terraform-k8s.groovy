podtemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: terraform
  name: terraform
spec:
  containers:
  - args:
    - sleep
    - "10000000"
    image: ikambarov/terraform:0.14
    name: terraform'''


podTemplate(cloud: 'kubernetes', label: 'terraform', showRawYaml: false, yaml: podtemplate ) {
    node('terraform'){
        container('terraform'){
            stage("Pull Repo"){
                git branch: 'solution', url: 'https://github.com/ikambarov/terraform-task.git'
            }
            
            stage("Test"){
                sh "terraform version"
            }
        }
    }
}
