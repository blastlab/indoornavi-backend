# Coordinates-calculator

## Budowanie

`docker build -t indoornavi.azurecr.io/coordinates-calculator -f Dockerfile.prod .`

Przed zbudowaniem obrazu dockerowego konieczne jest wcześniejsze zbudowanie aplikacji coordinates-calculatora komendą: `mvn clean install spring-boot:repackage`, ponieważ podczas budowania obrazu plik `jar` jest kopiowany do obrazu z folderu `target/`.


Aplikacja podczas budowania musi połączyć się z bazą danych, dlatego przed zbudowaniem konieczne jest uruchomienie aplikacji Indoornavi. Z tego powodu do budowania obrazu nie zastosowano [wieloetapowego pliku Dockerfile](https://docs.docker.com/develop/develop-images/multistage-build/).