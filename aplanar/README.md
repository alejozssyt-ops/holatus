# Aplanar

Mod de Fabric para Minecraft **1.21.11** que permite seleccionar un area cuadrada
y aplanar/minar el terreno.

- Minecraft: `1.21.11`
- Fabric Loader: `0.19.3`
- Fabric API: `0.141.6+1.21.11`
- Java: 21
- Mapeos: Mojang (oficiales)
- Mod Menu: opcional (`17.0.1`) para la pantalla de configuracion

## Como funciona

1. Manten pulsada la tecla de activacion (por defecto **click de la rueda del raton**).
2. Apunta a un bloque.
   - Cara superior/inferior: el area es un plano horizontal.
   - Cara lateral: el area es un plano vertical.
3. Mientras mantienes la tecla, gira la **rueda** para cambiar el lado del cuadrado.
4. Se muestra un HUD con el lado del cuadrado y cuantos bloques se minaran.
   Los bloques de aire no se cuentan. El contorno dibuja solo el perimetro
   exterior del area completa, no una caja por bloque.
5. Sin soltar la tecla, haz click izquierdo (accion de minar) para minar el area.
   Primero se mina el bloque central con el tiempo real que tardarias segun la
   herramienta; al romperlo, el resto del area se mina al instante. Cada bloque
   gasta durabilidad de la herramienta y respeta la regla de Irrompibilidad.

El aspecto del contorno se cambia desde la configuracion del mod en **Mod Menu**
(Archivos > `config/aplanar.json` si prefieres editarlo a mano):

- Modo de color **solido** (un color personalizable) o **animado**.
- Paleta de **2 a 6 colores** que se desplazan por el perimetro del area.
- Velocidad de animacion, brillo, grosor y opacidad del contorno.

El area maxima permitida es de **333 x 333** bloques y el tamano siempre es
impar, por lo que el bloque al que apuntas es siempre el centro exacto. Los
bloques restantes tras romper el central se procesan por lotes para que areas
grandes no bloqueen el servidor durante un solo tick. Todos los objetos que
suelten los bloques del area caen en el centro, donde se rompio el bloque
apuntado.

## Compilar

Necesitas **JDK 21**. Con el JDK instalado:

```bash
./gradlew build
```

El jar queda en `build/libs/aplanar-1.0.0.jar`.

En este equipo no habia Java instalado. Si aun no lo tienes:

```bash
sudo apt update && sudo apt install -y openjdk-21-jdk
```

## Probar en desarrollo

```bash
./gradlew runClient
./gradlew runServer
```

La primera vez el servidor de desarrollo pedira aceptar la EULA
(`run/eula.txt`).

## Estructura

```
src/main/java/com/alejozss/aplanar/
  AplanarMod.java          Registro del payload y ticks del servidor
  AplanarArea.java         Calculo de las posiciones del cuadrado
  AplanarMining.java       Minado progresivo con tiempos reales por bloque
  AplanarConfig.java       Configuracion (paleta, animacion y tamano maximo)
  net/BreakAreaPayload.java Paquete cliente -> servidor
src/client/java/com/alejozss/aplanar/client/
  AplanarClient.java       Teclas, seleccion y envio de minado
  AplanarHud.java          HUD con lado y numero de bloques
  SelectionRenderer.java   Contorno del perimetro del area
  AplanarColors.java       Muestreo y animacion de la paleta de colores
  AplanarConfigScreen.java Pantalla de configuracion
  AplanarModMenu.java      Integracion con Mod Menu
  mixin/MouseHandlerMixin.java  Rueda del raton mientras esta activo
```

## Notas

- El minado se hace en el servidor mediante un paquete. En un servidor que no
  tenga el mod instalado no se minara el area (solo en un jugador o servidores
  con el mod).
- El HUD y el contorno son del lado del cliente.
