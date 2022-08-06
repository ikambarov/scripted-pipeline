podtemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: k8s-tools
  name: k8s-tools
spec:
  serviceAccount: k8s-tools
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
  containers:
  - args:
    - sleep
    - "10000000"
    image: ikambarov/k8s-tools
    name: k8s-tools
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
      '''

podTemplate(cloud: 'kubernetes', label: 'k8s-tools', showRawYaml: false, yaml: podtemplate ) {
    node('k8s-tools'){
        container('k8s-tools'){
            stage("Pull repo"){
                git 'https://github.com/ikambarov/Flaskex-docker.git'
            }

            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                stage("Build"){
                    sh 'docker build -t $DOCKER_USERNAME/flaskex .'
                }

                stage("Push"){
                    sh '''
                        docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
                        docker push $DOCKER_USERNAME/flaskex 
                    '''
                }
            }

            stage("Deploy with Helm"){
                sh "rm -rf *"
                git 'https://github.com/ikambarov/flaskex-chart.git'
                sh '''
                    helm upgrade --install myapp . -n default
                '''
            }
        }
    }
}
