SERVER=server/main
CLIENT=client/main
COMP=kotlinc
ARG=-include-runtime -d

all: compServer compClient
	echo -n
runS: compServer
	java -jar server.jar ./.shared 9999

runC: compClient
	java -jar client.jar localhost:9999

compServer:
	$(COMP) server/server.kt server/Server.kt Interface.kt client/Client.kt $(ARG) server.jar

compClient:
	$(COMP) client/client.kt Interface.kt client/Client.kt $(ARG) server.jar



test:
	$(COMP) teste.kt $(ARG) teste.jar && java -jar teste.jar

clean: 
	rm *.jar
