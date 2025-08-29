# Reto DevExperte

## Estructura del Proyecto
El proyecto está organizado siguiendo una arquitectura limpia:

- Data: Implementa repositorios y fuentes de datos
- Domain: Contiene los modelos y casos de uso
- UI: Pantallas de la aplicación usando Jetpack Compose

# Implementación día 5

## Sistema de Inyección de Dependencias
La aplicación utiliza AppModule como sistema de inyección de dependencias manual

Este objeto centraliza la creación de dependencias y facilita su acceso desde cualquier parte de la aplicación
Para obtener el contexto que utiliza ScanCounterDataSoruceImpl cree una funcion para inicializar por asi decirlo 
y obtener el contexto de MainActivity

## Testing
He implementado dos test:
- TicketDataSourceFake: Simula un ticket con productos de comida mexicana
- ProcessDataUseCaseTest: Verifica:
  - El número correcto de productos en el ticket (3)
  - La suma total del ticket (280.00)