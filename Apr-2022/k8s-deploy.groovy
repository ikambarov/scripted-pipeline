template = '''
apiVersion: v1
kind: Pod
metadata:
  name: kubernetes
spec:
  serviceAccount: k8s-deploy
  containers:
  - args:
    - sleep
    - "100000"
    image: ikambarov/k8s-tools:v1
    name: kubernetes
    '''

podTemplate(cloud: 'kubernetes', label: 'kubernetes', showRawYaml: false, yaml: template) {
    node("kubernetes"){
        container("kubernetes"){
            stage("Pull Repo"){
                git "https://github.com/ikambarov/docker-melodi.git"
            }

            stage("Test"){
                sh "helm install web-app melodi-chart/"
            }
        }
    }
}