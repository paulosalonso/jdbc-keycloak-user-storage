# Keycloak JDBC User Storage

Este projeto implementa um __user storage__ para busca de usuários em um banco de dados com o Keycloak.

## Documentação

A documentação sobre como implementar um user storage está [nesta página](https://www.keycloak.org/docs/latest/server_development/#_user-storage-spi).

## Qualidade

[![Automated Testing](https://github.com/paulosalonso/jdbc-keycloak-user-storage/actions/workflows/automated-testing.yml/badge.svg)](https://github.com/paulosalonso/jdbc-keycloak-user-storage/actions/workflows/automated-testing.yml) [![Mutation Testing](https://github.com/paulosalonso/jdbc-keycloak-user-storage/actions/workflows/mutation-testing.yml/badge.svg)](https://github.com/paulosalonso/jdbc-keycloak-user-storage/actions/workflows/mutation-testing.yml)

A qualidade do código é garantida através de testes unitários e testes de mutação.

## Como usar

### Pacote JAR

#### GitHub

[![Release Package](https://github.com/paulosalonso/jdbc-keycloak-user-storage/actions/workflows/publish-package.yml/badge.svg?branch=1.0.0)](https://github.com/paulosalonso/jdbc-keycloak-user-storage/actions/workflows/publish-package.yml)

Baixe o JAR compactado [aqui](https://github.com/paulosalonso/jdbc-keycloak-user-storage/suites/2584381410/artifacts/56396756)

#### Local

O primeiro passo é gerar o pacote do projeto:

> mvn clean package

Além do artefato padrão, será gerado um artefato na pasta __./.docker/providers__.

### Containers Docker

Na raiz do projeto há uma pasta chamada __*.docker*__, a qual contém um __docker-compose__ que inicializa um container do Keycloak e um do MySQL.  
A configuração do Keycloak no docker-compose usa o MySQL para armazenamento dos dados, e mapeia a pasta __*providers*__ no diretório onde o Keycloak reconhece e instala os providers.  
Para o Keycloak são configurados um realm (conceito do Keycloak equivalente a um tenant) chamado __Provider__, um client chamado __provider__, e o __*user federation*__ que utiliza nosso artefato para buscar os usuários no banco de dados.  
Para o MySQL é configurada uma base chamada __keycloak__, com alguns usuários pré-cadastrados.

Depois de gerado o pacote, basta iniciar o docker-compose:

> docker-compose up -d

A inicialização leva um tempo, então é legal verificar os logs para ver quando tudo estiver pronto:

> docker-compose logs -f keycloak

A inicialização estará completa quando um log como o abaixo for exibido:

> INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990

### Painel administrativo

O painel administrativo do Keycloak pode ser acessado no endereço abaixo:

> http://localhost:8050

Ná pagina, clicar em __Administration Console__ e utilizar como usuário: __admin__ e senha: __admin__

### Obter um token

No diretório __*.postman*__ existe uma coleção do Postman. Basta importá-la e executar a request. Também é possível obter um token com o comando __curl__ abaixo:

> curl --location --request POST 'http://localhost:8050/auth/realms/Provider/protocol/openid-connect/token' \\ \
> --header 'Authorization: Basic cHJvdmlkZXI6NDMwNDFiZTctZDYxZC00OWQwLWJjZmYtODZhN2MyY2E2ZjAw' \\ \
> --header 'Content-Type: application/x-www-form-urlencoded' \\ \
> --data-urlencode 'grant_type=password' \\ \
> --data-urlencode 'username=joao.ger@algafood.com' \\ \
> --data-urlencode 'password=123' \\ \
> --data-urlencode 'client_id=provider'
