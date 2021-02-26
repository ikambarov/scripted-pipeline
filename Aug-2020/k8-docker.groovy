def k8slabel = "jenkins-agent-1"

def slavePodTemplate="""
metadata:
  labels:
    k8s-label: ${k8slabel}
spec:
  affinity:
    podAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
          - key: component
            operator: In
            values:
            - jenkins-jenkins-master
        topologyKey: "kubernetes.io/hostname"
  containers:
  - name: docker
    image: docker
    imagePullPolicy: IfNotPresent
    command:
    - sleep
    - "10000"
    volumeMounts:
      - mountPath: /var/run/docker.sock
        name: docker-sock
  serviceAccountName: default
  volumes:
    - name: docker-sock
      hostPath:
        path: /var/run/docker.sock

"""

podTemplate(name: k8slabel, label: k8slabel, yaml: slavePodTemplate, showRawYaml: false) {
  node(k8slabel){
    stage("Pull repo"){
      container("docker"){
        git url: 'https://github.com/ikambarov/Flaskex-docker.git'
      }
    }

    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USERNAME')]) {
      stage('Docker build'){
        container("docker"){
          sh "docker build -t ${DOCKERHUB_USERNAME}/flaskex ."
        }     
      }

      stage('Docker login'){
        container("docker"){
          sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
        }     
      }

      stage('Docker push'){
        container("docker"){
          sh "docker push ${DOCKERHUB_USERNAME}/flaskex"
        }     
      }

      stage('Trigger Helm install'){
        build wait: false, propagate: false, job: 'helm-flaskex'
      }
    }
    
  }
}
