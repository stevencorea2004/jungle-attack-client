package iu;

import java.awt.Graphics;
    import java.awt.Graphics2D;
    import java.awt.BasicStroke;
    import java.awt.Color;
    import java.awt.GradientPaint;
    import java.awt.Point;
    import java.awt.Rectangle;
    import java.awt.RenderingHints;
    import java.awt.geom.AffineTransform;
    import java.awt.image.BufferedImage;
    import java.awt.color.ColorSpace;
    import java.awt.image.ColorConvertOp;
    import java.util.Vector;
    import javax.swing.JPanel;
    import javax.swing.Timer;
    import model.AnimalCliente;
    import model.ModeloJuego;
    import model.Punto;
    import model.Seleccion;
    import red.VistaCamino;
    import red.VistaMapa;
    import red.VistaZona;
    import util.Constantes;

    public class PanelMapa extends JPanel {
        private static final int MUNDO_ANCHO = 1280;
        private static final int MUNDO_ALTO = 720;
        private ModeloJuego modelo;
        private Seleccion seleccion;
        private Vector<IDibujable> dibujables;
        private Timer refresco;
        private Rectangle seleccionVisual;
        private Integer animalBajoCursor;
        private final ControladorMouse controladorMouse;

        public PanelMapa(ModeloJuego modelo, Seleccion seleccion, control.ControladorJuego controlador) {
            this.modelo = modelo;
            this.seleccion = seleccion;
            this.dibujables = new Vector<>();
            this.controladorMouse = new ControladorMouse(controlador, this);
            setOpaque(true);
            setBackground(new Color(24, 65, 44));
            setPreferredSize(new java.awt.Dimension(900, 600));
            setFocusable(true);
            addMouseListener(controladorMouse);
            addMouseMotionListener(controladorMouse);
            refresco = new Timer(1000 / Constantes.FPS, e -> repaint());
            refresco.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (esZonaConNiebla()) {
                BufferedImage escena = new BufferedImage(
                        Math.max(1, getWidth()), Math.max(1, getHeight()), BufferedImage.TYPE_INT_ARGB);
                Graphics2D escenaGraphics = escena.createGraphics();
                dibujarEscena(escenaGraphics);
                dibujarNiebla(escenaGraphics);
                escenaGraphics.dispose();
                BufferedImage escalaGrises = new BufferedImage(
                        escena.getWidth(), escena.getHeight(), BufferedImage.TYPE_INT_ARGB);
                new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
                        .filter(escena, escalaGrises);
                g2.drawImage(escalaGrises, 0, 0, null);
                g2.dispose();
                return;
            }
            dibujarEscena(g2);
            g2.dispose();
        }

        private void dibujarEscena(Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.scale((double) getWidth() / MUNDO_ANCHO, (double) getHeight() / MUNDO_ALTO);
            ajustarARegion(g2);
            dibujarBosque(g2);
            VistaMapa mapa = modelo == null ? null : modelo.getMapa();
            if (mapa == null || mapa.caminos == null || mapa.caminos.isEmpty()) {
                dibujarCaminos(g2);
            } else {
                dibujarMapa(g2, mapa);
            }

            g2.setColor(new Color(248, 242, 207, 220));
            g2.setFont(g2.getFont().deriveFont(java.awt.Font.BOLD, 15f * escalaDetalle()));
            String zona = modelo == null || modelo.getZonaActual() == 0
                    ? "Exploracion del bosque" : "Zona " + modelo.getZonaActual();
            VistaZona region = zonaActual();
            int etiquetaX = region == null ? 28 : (int) (region.x + region.ancho * 0.04);
            int etiquetaY = region == null ? 38 : (int) (region.y + region.alto * 0.12);
            g2.drawString(zona, etiquetaX, etiquetaY);

            if (modelo != null) {
                long ahora = System.currentTimeMillis();
                for (Object taza : modelo.getTazas()) {
                    if (taza instanceof Punto) {
                        dibujarSprite(g2, new SpriteTaza((Punto) taza), (Punto) taza, ahora);
                    }
                }
                Punto bandera = modelo.getBandera();
                if (bandera != null) {
                    dibujarSprite(g2, new SpriteBandera(bandera, modelo.getColorBandera()), bandera, ahora);
                }
                for (AnimalCliente animal : modelo.getAnimales().values()) {
                    dibujarSprite(g2, new SpriteAnimal(animal, seleccion,
                            animal != null && animalBajoCursor != null
                                    && animalBajoCursor == animal.getId()),
                            animal == null ? null : animal.posInterpolada(ahora), ahora);
                }
                BufferedImage iconoAtaque = CacheSvg.getInstancia().getImagen("svg/atacando.svg");
                for (AnimalCliente animal : modelo.getAnimales().values()) {
                    if (animal == null || !animal.isAtacando() || animal.getIdObjetivo() == null) {
                        continue;
                    }
                    AnimalCliente objetivo = modelo.getAnimales().get(animal.getIdObjetivo());
                    if (objetivo == null
                            && (animal.getObjetivoX() == null || animal.getObjetivoY() == null)) {
                        continue;
                    }
                    Punto posicionAtacante = animal.posInterpolada(ahora);
                    Punto posicionObjetivo = objetivo == null
                            ? new Punto(animal.getObjetivoX(), animal.getObjetivoY())
                            : objetivo.posInterpolada(ahora);
                    int x = (int) Math.round((posicionAtacante.getX() + posicionObjetivo.getX()) / 2.0);
                    int y = (int) Math.round((posicionAtacante.getY() + posicionObjetivo.getY()) / 2.0);
                    Graphics2D iconGraphics = graficosDeSprite(g2, new Punto(x, y));
                    iconGraphics.drawImage(iconoAtaque, x - 16, y - 16, 32, 32, null);
                    iconGraphics.dispose();
                }
            }

            if (seleccionVisual != null) {
                g2.setColor(new Color(Constantes.COLOR_SELECCION.getRed(),
                        Constantes.COLOR_SELECCION.getGreen(), Constantes.COLOR_SELECCION.getBlue(), 45));
                g2.fill(seleccionVisual);
                g2.setColor(Constantes.COLOR_SELECCION);
                g2.setStroke(new BasicStroke(2f * escalaDetalle()));
                g2.draw(seleccionVisual);
            }
        }

        private boolean esZonaConNiebla() {
            return modelo != null && modelo.getMapa() != null
                    && modelo.getZonaActual() != 0
                    && (modelo.getZonasVisibles() == null
                    || !modelo.getZonasVisibles().contains(modelo.getZonaActual()));
        }

        private void dibujarNiebla(Graphics2D g2) {
            BufferedImage niebla = CacheSvg.getInstancia().getImagen("svg/niebla.svg");
            int lado = Math.max(1, (int) (Math.min(getWidth(), getHeight()) * 0.32));
            int x = (getWidth() - lado) / 2;
            int y = (getHeight() - lado) / 2;
            AffineTransform transformacionAnterior = g2.getTransform();
            g2.setTransform(new AffineTransform());
            g2.drawImage(niebla, x, y, lado, lado, null);
            g2.setTransform(transformacionAnterior);
        }

        public Punto pantallaAMundo(Point p) {
            if (p == null || getWidth() <= 0 || getHeight() <= 0) {
                return new Punto();
            }
            VistaZona region = zonaActual();
            if (region != null && region.ancho > 0.0 && region.alto > 0.0) {
                return new Punto(region.x + (double) p.x * region.ancho / getWidth(),
                        region.y + (double) p.y * region.alto / getHeight());
            }
            return new Punto((double) p.x * MUNDO_ANCHO / getWidth(),
                    (double) p.y * MUNDO_ALTO / getHeight());
        }

        public Point mundoAPantalla(Punto p) {
            if (p == null) {
                return new Point();
            }
            VistaZona region = zonaActual();
            if (region != null && region.ancho > 0.0 && region.alto > 0.0) {
                return new Point(
                        (int) Math.round((p.getX() - region.x) * getWidth() / region.ancho),
                        (int) Math.round((p.getY() - region.y) * getHeight() / region.alto));
            }
            return new Point((int) Math.round(p.getX() * getWidth() / MUNDO_ANCHO),
                    (int) Math.round(p.getY() * getHeight() / MUNDO_ALTO));
        }

        public AnimalCliente animalCercano(Point pantalla) {
            Punto mundo = pantallaAMundo(pantalla);
            AnimalCliente cercano = null;
            double menorDistancia = 34.0;
            if (modelo == null) {
                return null;
            }
            for (AnimalCliente animal : modelo.getAnimales().values()) {
                if (animal == null) {
                    continue;
                }
                Punto posicion = animal.posInterpolada(System.currentTimeMillis());
                double distancia = pantalla.distance(mundoAPantalla(posicion));
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    cercano = animal;
                }
            }
            return cercano;
        }

        public void actualizarAnimalBajoCursor(Point pantalla) {
            AnimalCliente animal = pantalla == null ? null : animalCercano(pantalla);
            Integer nuevoId = animal == null ? null : animal.getId();
            if (!java.util.Objects.equals(animalBajoCursor, nuevoId)) {
                animalBajoCursor = nuevoId;
                repaint();
            }
        }

        public Rectangle rectanguloMundo(Point inicioPantalla, Point finPantalla) {
            Punto inicio = pantallaAMundo(inicioPantalla);
            Punto fin = pantallaAMundo(finPantalla);
            int x = (int) Math.floor(Math.min(inicio.getX(), fin.getX()));
            int y = (int) Math.floor(Math.min(inicio.getY(), fin.getY()));
            int ancho = (int) Math.ceil(Math.abs(fin.getX() - inicio.getX()));
            int alto = (int) Math.ceil(Math.abs(fin.getY() - inicio.getY()));
            return new Rectangle(x, y, ancho, alto);
        }

        public void setSeleccionVisual(Point inicioPantalla, Point finPantalla) {
            if (inicioPantalla == null || finPantalla == null) {
                seleccionVisual = null;
            } else {
                seleccionVisual = rectanguloMundo(inicioPantalla, finPantalla);
            }
            repaint();
        }

        public ModeloJuego getModelo() {
            return modelo;
        }

        public Seleccion getSeleccion() {
            return seleccion;
        }

        public void detenerAnimacion() {
            if (refresco != null) {
                refresco.stop();
            }
        }

        private void ajustarARegion(Graphics2D g2) {
            VistaZona region = zonaActual();
            if (region != null && region.ancho > 0.0 && region.alto > 0.0) {
                g2.scale(MUNDO_ANCHO / region.ancho, MUNDO_ALTO / region.alto);
                g2.translate(-region.x, -region.y);
            }
        }

        private VistaZona zonaActual() {
            if (modelo == null || modelo.getMapa() == null || modelo.getMapa().zonas == null) {
                return null;
            }
            for (VistaZona zona : modelo.getMapa().zonas) {
                if (zona != null && zona.id == modelo.getZonaActual()) {
                    return zona;
                }
            }
            return null;
        }

        private float escalaDetalle() {
            VistaZona region = zonaActual();
            if (region == null || region.ancho <= 0.0 || region.alto <= 0.0) {
                return 1f;
            }
            return (float) Math.min(region.ancho / MUNDO_ANCHO, region.alto / MUNDO_ALTO);
        }

        private Graphics2D graficosDeSprite(Graphics2D g2, Punto posicion) {
            Graphics2D spriteGraphics = (Graphics2D) g2.create();
            VistaZona region = zonaActual();
            if (region != null && posicion != null && region.ancho > 0.0 && region.alto > 0.0) {
                spriteGraphics.translate(posicion.getX(), posicion.getY());
                spriteGraphics.scale(region.ancho / MUNDO_ANCHO, region.alto / MUNDO_ALTO);
                spriteGraphics.translate(-posicion.getX(), -posicion.getY());
            }
            return spriteGraphics;
        }

        private void dibujarSprite(Graphics2D g2, Sprite sprite, Punto posicion, long ahora) {
            Graphics2D spriteGraphics = graficosDeSprite(g2, posicion);
            sprite.dibujar(spriteGraphics, ahora);
            spriteGraphics.dispose();
        }

        private void dibujarBosque(Graphics2D g2) {
            g2.setPaint(new GradientPaint(0, 0, new Color(48, 111, 68),
                    MUNDO_ANCHO, MUNDO_ALTO, new Color(24, 77, 54)));
            g2.fillRect(0, 0, MUNDO_ANCHO, MUNDO_ALTO);
            for (int y = 65; y < MUNDO_ALTO; y += 92) {
                for (int x = 30 + ((y / 92) % 2) * 45; x < MUNDO_ANCHO; x += 110) {
                    int seed = (x * 17 + y * 31) % 3;
                    g2.setColor(new Color(14, 55 + seed * 8, 39, 82));
                    g2.fillOval(x, y, 52, 32);
                    g2.setColor(new Color(178, 202, 130, 45));
                    g2.drawArc(x + 7, y + 4, 34, 20, 15, 150);
                }
            }
            g2.setColor(new Color(232, 220, 169, 26));
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1f, new float[] {8f, 12f}, 0f));
            for (int i = 1; i < 4; i++) {
                g2.drawLine(i * MUNDO_ANCHO / 4, 0, i * MUNDO_ANCHO / 4, MUNDO_ALTO);
            }
        }

        private void dibujarCaminos(Graphics2D g2) {
            float escala = escalaDetalle();
            int[][] camino = {
                {0, 520}, {155, 450}, {300, 500}, {460, 360}, {610, 390},
                {765, 270}, {900, 330}, {1030, 210}, {1280, 255}
            };
            g2.setColor(new Color(32, 59, 39, 110));
            g2.setStroke(new BasicStroke(48 * escala, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i < camino.length; i++) {
                g2.drawLine(camino[i - 1][0], camino[i - 1][1], camino[i][0], camino[i][1]);
            }
            g2.setColor(new Color(193, 174, 119, 180));
            g2.setStroke(new BasicStroke(32 * escala, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i < camino.length; i++) {
                g2.drawLine(camino[i - 1][0], camino[i - 1][1], camino[i][0], camino[i][1]);
            }
            g2.setColor(new Color(226, 210, 160, 125));
            g2.setStroke(new BasicStroke(2f * escala, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1f, new float[] {10f * escala, 12f * escala}, 0f));
            for (int i = 1; i < camino.length; i++) {
                g2.drawLine(camino[i - 1][0], camino[i - 1][1], camino[i][0], camino[i][1]);
            }
            g2.setStroke(new BasicStroke(1f));
        }

        private void dibujarMapa(Graphics2D g2, VistaMapa mapa) {
            float escala = escalaDetalle();
            g2.setColor(new Color(32, 59, 39, 110));
            g2.setStroke(new BasicStroke(48 * escala, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (VistaCamino camino : mapa.caminos) {
                dibujarCamino(g2, camino);
            }
            g2.setColor(new Color(193, 174, 119, 180));
            g2.setStroke(new BasicStroke(32 * escala, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (VistaCamino camino : mapa.caminos) {
                dibujarCamino(g2, camino);
            }
            g2.setColor(new Color(226, 210, 160, 125));
            g2.setStroke(new BasicStroke(2f * escala, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1f, new float[] {10f * escala, 12f * escala}, 0f));
            for (VistaCamino camino : mapa.caminos) {
                dibujarCamino(g2, camino);
            }

            if (mapa.zonas != null) {
                g2.setColor(new Color(232, 220, 169, 80));
                g2.setStroke(new BasicStroke(1f * escala, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        1f, new float[] {5f * escala, 8f * escala}, 0f));
                for (VistaZona zona : mapa.zonas) {
                    if (zona == null) {
                        continue;
                    }
                    g2.drawRect((int) zona.x, (int) zona.y, (int) zona.ancho, (int) zona.alto);
                    g2.setFont(g2.getFont().deriveFont(java.awt.Font.PLAIN, 11f * escala));
                    g2.drawString("Zona " + zona.id, (int) zona.x + 8, (int) zona.y + 18);
                }
            }
            g2.setStroke(new BasicStroke(1f));
        }

        private void dibujarCamino(Graphics2D g2, VistaCamino camino) {
            if (camino == null || camino.puntos == null) {
                return;
            }
            for (int i = 1; i < camino.puntos.size(); i++) {
                Punto anterior = camino.puntos.get(i - 1);
                Punto siguiente = camino.puntos.get(i);
                if (anterior != null && siguiente != null) {
                    g2.drawLine((int) anterior.getX(), (int) anterior.getY(),
                            (int) siguiente.getX(), (int) siguiente.getY());
                }
            }
        }
}
