pod = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: terraform
  name: terraform
spec:
  containers:
  - image: ikambarov/terraform:0.14
    name: terraform
'''

podTemplate(cloud: 'kubernetes', label: 'terraform', name: 'terraform', yaml: pod ) {
    node('terraform'){
        stage("Check Terraform Version"){
            container('terraform'){
                sh "terraform version"
            }
        }
    }
}
