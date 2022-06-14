pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                sh 'terraform version'
            }
        }
    }
}

