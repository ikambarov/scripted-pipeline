node{
    stage("Pull"){
        git 'https://github.com/ikambarov/spring-petclinic.git'
    }
    stage("Build"){
        sh '''
            echo "Build"
        '''
    }
    stage("Test"){
        sh '''
            echo "Test"
        '''
        
        input 'Do you want to continue?'
    }
    stage("Deploy"){
        sh '''
            echo "Deploy"
        '''
    }
}

