SERVER=server/main
CLIENT=client/main
COMP=kotlinc
ARG=-include-runtime -d






all: compServer compClient
	1 2> /dev/null

runS: compServer
	java -jar server.jar ./.shared 9999

runC: compClient
	java -jar client.jar localhost:9999

compServer:
	$(COMP) server.kt Server.kt Interface.kt Client.kt $(ARG) server.jar

compClient:
	$(COMP) client.kt Interface.kt Client.kt $(ARG) server.jar



test:
	$(COMP) teste.kt $(ARG) teste.jar && java -jar teste.jar

clean: 
	rm *.jar
