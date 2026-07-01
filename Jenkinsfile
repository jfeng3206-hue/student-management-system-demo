pipeline {
    agent any

    options {
        skipStagesAfterUnstable()
        timestamps()
    }

    stages {
        stage('Build on dev') {
            when {
                branch 'dev'
            }
            steps {
                sh '''
                    export PATH=/opt/java/openjdk/bin:$PATH
                    chmod +x ./mvnw
                    ./mvnw -DskipTests package
                '''
            }
        }
    }
}
