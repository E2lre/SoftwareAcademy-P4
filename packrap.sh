Echo
Echo ****************************Clean****************************
Echo
mvn clean
Echo
Echo ****************************Package****************************
Echo
mvn package --quiet
Echo
Echo ****************************Surfire****************************
Echo
mvn surefire-report:report
Echo
Echo ****************************Jacoco****************************
Echo
mvn jacoco:report
Echo
Echo ****************************Spotbugs****************************
Echo
mvn site --quiet