podTemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    agent: terraform
  name: terraform
spec:
  containers:
  - image: ikambarov/terraform:0.14
    name: terraform
'''

podTemplate(cloud: 'kubernetes', label: 'terraform', showRawYaml: false, yaml: podTemplate) {
    node('terraform'){
        container('terraform'){
            stage("Version"){
                sh "terraform version"
            }
        }
    }
}