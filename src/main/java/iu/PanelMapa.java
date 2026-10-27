package iu;

import java.awt.Graphics;
    import java.awt.Graphics2D;
    import java.awt.BasicStroke;
    import java.awt.Color;
    import java.awt.GradientPaint;
    import java.awt.Point;
    import java.awt.Rectangle;
    import java.awt.RenderingHints;
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
        private Timer refresco;
        private Rectangle seleccionVisual;
        private final ControladorMouse controladorMouse;

        public PanelMapa(ModeloJuego modelo, Seleccion seleccion, control.ControladorJuego controlador) {
            this.modelo = modelo;
            this.seleccion = seleccion;
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.scale((double) getWidth() / MUNDO_ANCHO, (double) getHeight() / MUNDO_ALTO);
            dibujarBosque(g2);
            VistaMapa mapa = modelo == null ? null : modelo.getMapa();
            if (mapa == null || mapa.caminos == null || mapa.caminos.isEmpty()) {
                dibujarCaminos(g2);
            } else {
                dibujarMapa(g2, mapa);
            }

            g2.setColor(new Color(248, 242, 207, 220));
            g2.setFont(g2.getFont().deriveFont(java.awt.Font.BOLD, 15f));
            String zona = modelo == null || modelo.getZonaActual() == 0
                    ? "Exploracion del bosque" : "Zona " + modelo.getZonaActual();
            g2.drawString(zona, 28, 38);

            if (modelo != null) {
                for (Object taza : modelo.getTazas()) {
                    if (taza instanceof Punto) {
                        new SpriteTaza((Punto) taza).dibujar(g2, System.currentTimeMillis());
                    }
                }
                Punto bandera = modelo.getBandera();
                if (bandera != null) {
                    new SpriteBandera(bandera, modelo.getColorBandera())
                            .dibujar(g2, System.currentTimeMillis());
                }
                for (AnimalCliente animal : modelo.getAnimales().values()) {
                    new SpriteAnimal(animal, seleccion).dibujar(g2, System.currentTimeMillis());
                }
            }

            if (seleccionVisual != null) {
                g2.setColor(new Color(Constantes.COLOR_SELECCION.getRed(),
                        Constantes.COLOR_SELECCION.getGreen(), Constantes.COLOR_SELECCION.getBlue(), 45));
                g2.fill(seleccionVisual);
                g2.setColor(Constantes.COLOR_SELECCION);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(seleccionVisual);
            }
            g2.dispose();
        }

        public Punto pantallaAMundo(Point p) {
            if (p == null || getWidth() <= 0 || getHeight() <= 0) {
                return new Punto();
            }
            return new Punto((double) p.x * MUNDO_ANCHO / getWidth(),
                    (double) p.y * MUNDO_ALTO / getHeight());
        }

        public Point mundoAPantalla(Punto p) {
            if (p == null) {
                return new Point();
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
                double distancia = mundo.distancia(posicion);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    cercano = animal;
                }
            }
            return cercano;
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
            int[][] camino = {
                {0, 520}, {155, 450}, {300, 500}, {460, 360}, {610, 390},
                {765, 270}, {900, 330}, {1030, 210}, {1280, 255}
            };
            g2.setColor(new Color(32, 59, 39, 110));
            g2.setStroke(new BasicStroke(48, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i < camino.length; i++) {
                g2.drawLine(camino[i - 1][0], camino[i - 1][1], camino[i][0], camino[i][1]);
            }
            g2.setColor(new Color(193, 174, 119, 180));
            g2.setStroke(new BasicStroke(32, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i < camino.length; i++) {
                g2.drawLine(camino[i - 1][0], camino[i - 1][1], camino[i][0], camino[i][1]);
            }
            g2.setColor(new Color(226, 210, 160, 125));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1f, new float[] {10f, 12f}, 0f));
            for (int i = 1; i < camino.length; i++) {
                g2.drawLine(camino[i - 1][0], camino[i - 1][1], camino[i][0], camino[i][1]);
            }
            g2.setStroke(new BasicStroke(1f));
        }

        private void dibujarMapa(Graphics2D g2, VistaMapa mapa) {
            g2.setColor(new Color(32, 59, 39, 110));
            g2.setStroke(new BasicStroke(48, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (VistaCamino camino : mapa.caminos) {
                dibujarCamino(g2, camino);
            }
            g2.setColor(new Color(193, 174, 119, 180));
            g2.setStroke(new BasicStroke(32, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (VistaCamino camino : mapa.caminos) {
                dibujarCamino(g2, camino);
            }
            g2.setColor(new Color(226, 210, 160, 125));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1f, new float[] {10f, 12f}, 0f));
            for (VistaCamino camino : mapa.caminos) {
                dibujarCamino(g2, camino);
            }

            if (mapa.zonas != null) {
                g2.setColor(new Color(232, 220, 169, 80));
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        1f, new float[] {5f, 8f}, 0f));
                for (VistaZona zona : mapa.zonas) {
                    if (zona == null) {
                        continue;
                    }
                    g2.drawRect((int) zona.x, (int) zona.y, (int) zona.ancho, (int) zona.alto);
                    g2.setFont(g2.getFont().deriveFont(java.awt.Font.PLAIN, 11f));
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
