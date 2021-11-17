def podtemplate = '''
apiVersion: v1
kind: Pod
metadata:
  name: terraform
spec:
  containers:
  - image: ikambarov/terraform:0.14
    name: terraform
'''

podTemplate(label: 'terraform', name: 'terraform', namespace: 'tools', yaml: podtemplate, showRawYaml: false) {
    node("terraform"){
        container("terraform"){
            stage("Test"){
                sh "terraform version"
            }
        }
    }
}
