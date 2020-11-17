
def pod_name =  "jenkins-agent-${UUID.randomUUID().toString()}"

def pod_yaml = '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: jenkins-agent
  name: jenkins-agent
spec:
  affinity:
    podAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
        matchExpressions:
        - key: app.kubernetes.io/component
          operator: In
          values:
          - jenkins-master
        topologyKey: "kubernetes.io/hostname"
  containers:
  - image: docker
    name: docker
    command:
    - sleep
    - "10000"
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
'''

podTemplate(label: pod_name, name: pod_name, namespace: 'default', yaml: pod_yaml) {
    node(pod_name){
        container('docker'){
            stage("Pull Repo"){
                git url: 'https://github.com/ikambarov/Flaskex-docker.git'
            }
            
            withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', passwordVariable: 'REGISTRY_PASSWORD', usernameVariable: 'REGISTRY_USERNAME')]) {
                stage("Docker Build"){
                    sh "docker build -t ${REGISTRY_USERNAME}/flaskex  ."
                }
                
                stage("Docker Login"){
                    sh "docker login -u ${REGISTRY_USERNAME} -p '${REGISTRY_PASSWORD}'"
                }

                stage("Docker Push"){
                    sh "docker push ${REGISTRY_USERNAME}/flaskex"
                }

                stage("Cleanup"){
                    sh "docker rmi ${REGISTRY_USERNAME}/flaskex"
                }
            }
        }
    }
}