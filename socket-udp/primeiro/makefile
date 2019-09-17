COMP=kotlinc
ARG=-include-runtime -d

runS: compServer
	java -jar nameserver.jar 9999

runC: compClient
	java -jar peer.jar


compServer:
	$(COMP) ns/main.kt ns/NameServer.kt ns/Connection.kt Interface.kt $(ARG) nameserver.jar

compClient:
	$(COMP) peer/main.kt peer/Peer.kt Interface.kt ns/Connection.kt $(ARG) peer.jar


test:
	$(COMP) teste.kt $(ARG) teste.jar && java -jar teste.jar

clean: 
	rm *.jar
