podTemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    agent: kubectl
  name: kubectl
spec:
  serviceAccount: k8s-tools
  containers:
  - image: ikambarov/k8s-tools
    name: kubectl
'''

podTemplate(cloud: 'kubernetes', label: 'kubectl', showRawYaml: false, yaml: podTemplate) {
    node('kubectl'){
        container('kubectl'){
            stage("Version"){
                sh "kubectl version"
            }

            stage("List Pods"){
                sh "kubectl get pods -n default"
            }

            stage("Create Nginx Pod"){
                sh "kubectl run nginx-pod --image=nginx -n default --dry-run -o yaml | kubectl apply -f -"
            }
        }
    }
}