podTemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    agent: kubectl
  name: kubectl
spec:
  serviceAccount: k8s-tools
  containers:
  - image: ikambarov/k8s-tools:v3
    name: kubectl
'''

podTemplate(cloud: 'kubernetes', label: 'kubectl', showRawYaml: false, yaml: podTemplate) {
    node('kubectl'){
        container('kubectl'){
            stage("Version"){
                sh "helm version"
            }

            stage("List Pods"){
                sh "helm list"
            }

            stage("Deploy Flaskex"){
                git('https://github.com/ikambarov/flaskex-chart.git')
                sh "helm upgrade --install  myapp . -n default"
            }
        }
    }
}