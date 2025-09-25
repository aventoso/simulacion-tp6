package ar.utn.frba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

import static ar.utn.frba.Utils.*;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {


        LOGGER.info("=== Simulación: Cuantos desarrolladores se necesitan para solucionar hotfix ===");

        Scanner scanner = new Scanner(System.in);

        LOGGER.info("La simulación arranca el día 2025/01/01 a las 9:00 hrs");
        LOGGER.info("Ingrese el tiempo de finalización (formato: 2025/04/01 19:30): ");
        String tiempoFinalStr = "2025/04/01 19:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime tiempoFinal = LocalDateTime.parse(tiempoFinalStr, formatter);

        LOGGER.info("Cantidad de desarrolladores junior: ");
        int nj = scanner.nextInt();

        LOGGER.info("Cantidad de desarrolladores semi-senior: ");
        int nss = scanner.nextInt();

        LOGGER.info("Cantidad de desarrolladores senior: ");
        int ns = scanner.nextInt();

        // Variables de estado


        Variables.Estado estado = new Variables.Estado(nj, nss, ns);
        Variables.Control control = new Variables.Control(nj, nss, ns);
        Variables.Resultado resultado = new Variables.Resultado();


        Variables variables = new Variables(estado, control, resultado);

        int iteraciones = 0;
        boolean vaciamiento = false;

        // Loop principal
        while (variables.getT().isBefore(tiempoFinal)) {
            iteraciones++;
            int i = getMenorTps(variables.getEstado().getTpsJr());
            int j = getMenorTps(variables.getEstado().getTpsSsr());
            int k = getMenorTps(variables.getEstado().getTpsSr());

            variables.setI(i);
            variables.setJ(j);
            variables.setK(k);

            if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) &&
                Objects.nonNull(variables.getEstado().getTpsSsr()[j]) &&
                (variables.getEstado().getTpsJr()[i].isBefore(variables.getEstado().getTpsSsr()[j]) ||
                 variables.getEstado().getTpsJr()[i].equals(variables.getEstado().getTpsSsr()[j]))) {
                if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) &&
                    Objects.nonNull(variables.getEstado().getTpsSr()[k]) &&
                    (variables.getEstado().getTpsJr()[i].isBefore(variables.getEstado().getTpsSsr()[j]) ||
                     variables.getEstado().getTpsJr()[i].equals(variables.getEstado().getTpsSr()[k]))) {
                    if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) &&
                        (variables.getEstado().getTpsJr()[i].isBefore(variables.getTpll()) ||
                         variables.getEstado().getTpsJr()[i].equals(variables.getTpll()))) {
                        // Salida Junior
                        procesarSalidaJunior(variables, i);
                    } else {
                        procesarLlegada(variables);
                    }
                } else if (Objects.nonNull(variables.getEstado().getTpsSsr()[j]) &&
                           (variables.getEstado().getTpsSsr()[j].isBefore(variables.getTpll()) ||
                            variables.getEstado().getTpsSsr()[j].equals(variables.getTpll()))) {
                    // salida ssr
                    procesarSalidaSemiSenior(variables, j);
                } else {
                    procesarLlegada(variables);
                }

            } else if (Objects.nonNull(variables.getEstado().getTpsSsr()[j]) &&
                       Objects.nonNull(variables.getEstado().getTpsSr()[k]) &&
                       (variables.getEstado().getTpsSsr()[j].isBefore(variables.getEstado().getTpsSr()[k]) ||
                        variables.getEstado().getTpsSsr()[j].equals(variables.getEstado().getTpsSr()[k]))) {
                if (Objects.nonNull(variables.getEstado().getTpsSsr()[j]) &&
                    (variables.getEstado().getTpsSsr()[j].isBefore(variables.getTpll()))) {
                    // salida ssr
                    procesarSalidaSemiSenior(variables, j);
                } else {
                    procesarLlegada(variables);
                }
            } else if (Objects.nonNull(variables.getEstado().getTpsSr()[k]) &&
                       (variables.getEstado().getTpsSr()[k].isBefore(variables.getTpll()) ||
                        variables.getEstado().getTpsSr()[k].equals(variables.getTpll()))) {
                // salida senior
                procesarSalidaSenior(variables, k);
            } else {
                // llegada
                procesarLlegada(variables);
            }

            LOGGER.info(variables.getEstado().toString());
        }

        while (variables.getEstado().getCtb() != 0 || variables.getEstado().getCtm() != 0 ||
               variables.getEstado().getCta() != 0 || variables.getEstado().getCtc() != 0) {

            iteraciones++;

            Integer i = getMenorTpsIgnorandoNulls(variables.getEstado().getTpsJr());
            Integer j = getMenorTpsIgnorandoNulls(variables.getEstado().getTpsSsr());
            Integer k = getMenorTpsIgnorandoNulls(variables.getEstado().getTpsSr());

            variables.setI(i);
            variables.setJ(j);
            variables.setK(k);

            // Obtener los tiempos correspondientes
            LocalDateTime tI = (i != null) ? variables.getEstado().getTpsJr()[i] : null;
            LocalDateTime tJ = (j != null) ? variables.getEstado().getTpsSsr()[j] : null;
            LocalDateTime tK = (k != null) ? variables.getEstado().getTpsSr()[k] : null;

            // Inicializamos menorTiempo con el más pequeño de los tres (no nulos)
            LocalDateTime menorTiempo = null;
            if (tI != null)
                menorTiempo = tI;
            if (tJ != null && (menorTiempo == null || tJ.isBefore(menorTiempo)))
                menorTiempo = tJ;
            if (tK != null && (menorTiempo == null || tK.isBefore(menorTiempo)))
                menorTiempo = tK;

            // Ejecutar la acción según cuál es el menor
            if (tI != null && tI.equals(menorTiempo)) {
                procesarSalidaJunior(variables, i);
            } else if (tJ != null && tJ.equals(menorTiempo)) {
                procesarSalidaSemiSenior(variables, j);
            } else if (tK != null && tK.equals(menorTiempo)) {
                procesarSalidaSenior(variables, k);
            }

            LOGGER.info(variables.getEstado().toString());
        }

        // Resultados finales (placeholders porque en tu código Python estaban incompletos)
        LOGGER.info("\n=== Resultados ===");
        LOGGER.info("Iteraciones: {}", iteraciones);
        LOGGER.info("Fecha de inicio: {}", "2025-01-01T9:00:00");
        LOGGER.info("Fecha de finalizacion: {}", variables.getT().toString());

        variables.getResultado().calculateCTR().forEach((etiqueta, valor) -> {
            LOGGER.info("{} -> {}", etiqueta, valor);
        });
        variables.getResultado().calculateDTC().forEach((etiqueta, valor) -> {
            LOGGER.info("{} -> {}", etiqueta, valor);
        });
        variables.getResultado().calculateTPEC().forEach((etiqueta, valor) -> {
            LOGGER.info("{} -> {}", etiqueta, valor);
        });


        scanner.close();
    }

    private static void procesarLlegada(Variables var) {
        LocalDateTime tAux = var.getT();
        LocalDateTime tpllAux = var.getTpll();

        var.setT(var.getTpll());
        double ia = getIA();
        var.setTpll(var.getT().plusMinutes(Math.round(ia)));
        double r = Math.random();
        var.getResultado().setCtt(var.getResultado().getCtt() + 1);

        if (r < 0.2) {
            //criticidad baja
            var.getResultado().addStsB(tAux, tpllAux, var.getEstado().getCtb());
            var.getEstado().bajosEncoladosPlus();
            var.getEstado().setCtb(var.getEstado().getCtb() + 1);
            var.getResultado().setCttb(var.getResultado().getCttb() + 1);

            Integer indexJunior = getJuniorDisponible(var);

            if (Objects.nonNull(indexJunior)) {
                var.getEstado().getJuniorAtendiendo()[indexJunior] = "B";
                var.getEstado().bajosEncoladosMinus();
                double ta = getTA();
                ta = ta * 1.5;
                updateJuniorTps(var, indexJunior, ta);
                var.getResultado().addStaB(ta);
            } else {
                Integer indexSemiSenior = getSemiSeniorDisponible(var);
                if (Objects.nonNull(indexSemiSenior)) {
                    var.getEstado().getSemiSeniorAtendiendo()[indexSemiSenior] = "B";
                    var.getEstado().bajosEncoladosMinus();
                    double ta = getTA();
                    updateSemiSeniorTps(var, indexSemiSenior, ta);
                    var.getResultado().addStaB(ta);
                } else {
                    Integer indexSenior = getSeniorDisponible(var);
                    if (Objects.nonNull(indexSenior) && var.getEstado().getBajosEncolados() >= 2) {
                        var.getEstado().getSeniorAtendiendo()[indexSenior] = "B";
                        var.getEstado().bajosEncoladosMinus();
                        var.getEstado().bajosEncoladosMinus();
                        double ta = getTA();
                        double ta2 = getTA();
                        double tat = (ta + ta2) * 0.7;
                        updateSeniorTps(var, indexSenior, tat);
                        var.getResultado().addStaB(tat);
                    }

                }
            }
        } else if (r < 0.5) {
            //medios
            var.getResultado().addStsM(tAux, tpllAux, var.getEstado().getCtm());
            var.getEstado().mediosEncoladosPlus();
            var.getResultado().setCttm(var.getResultado().getCttm() + 1);
            var.getEstado().setCtm(var.getEstado().getCtm() + 1);


            Integer indexJunior = getJuniorDisponible(var);

            if (Objects.nonNull(indexJunior)) {
                var.getEstado().getJuniorAtendiendo()[indexJunior] = "M";
                var.getEstado().mediosEncoladosMinus();
                double ta = getTA();
                ta = ta * 1.5;
                updateJuniorTps(var, indexJunior, ta);
                var.getResultado().addStaM(ta);
            } else {
                Integer indexSemiSenior = getSemiSeniorDisponible(var);
                if (Objects.nonNull(indexSemiSenior)) {
                    var.getEstado().getSemiSeniorAtendiendo()[indexSemiSenior] = "M";
                    var.getEstado().mediosEncoladosMinus();
                    double ta = getTA();
                    updateSemiSeniorTps(var, indexSemiSenior, ta);
                    var.getResultado().addStaM(ta);
                } else {
                    Integer indexSenior = getSeniorDisponible(var);
                    if (Objects.nonNull(indexSenior)) {
                        var.getEstado().getSeniorAtendiendo()[indexSenior] = "M";
                        var.getEstado().mediosEncoladosMinus();
                        double ta = getTA();
                        ta = ta * 0.7;
                        updateSeniorTps(var, indexSenior, ta);
                        var.getResultado().addStaM(ta);

                    }

                }
            }
        } else if (r < 0.8) {
            // llega alto
            var.getResultado().addStsA(tAux, tpllAux, var.getEstado().getCta());
            var.getEstado().altosEncoladosPlus();
            var.getResultado().setCtta(var.getResultado().getCtta() + 1);
            var.getEstado().setCta(var.getEstado().getCta() + 1);


            Integer indexSSenior = getSemiSeniorDisponible(var);

            if (Objects.nonNull(indexSSenior)) {
                var.getEstado().getSemiSeniorAtendiendo()[indexSSenior] = "A";
                var.getEstado().altosEncoladosMinus();
                double ta = getTA();
                updateSemiSeniorTps(var, indexSSenior, ta);
                var.getResultado().addStaA(ta);
            } else {
                Integer indexSenior = getSeniorDisponible(var);
                if (Objects.nonNull(indexSenior)) {
                    var.getEstado().getSeniorAtendiendo()[indexSenior] = "A";
                    var.getEstado().altosEncoladosMinus();
                    double ta = getTA();
                    ta = ta * 0.7;
                    updateSeniorTps(var, indexSenior, ta);
                    var.getResultado().addStaA(ta);
                }
            }
        } else {
            //llega critico
            var.getResultado().addStsC(tAux, tpllAux, var.getEstado().getCtc());
            var.getEstado().criticosEncoladosPlus();
            var.getResultado().setCttc(var.getResultado().getCttc() + 1);
            var.getEstado().setCtc(var.getEstado().getCtc() + 1);

            Integer index = getSeniorDisponible(var);

            if (Objects.nonNull(index)) {
                var.getEstado().getSeniorAtendiendo()[index] = "C";
                var.getEstado().criticosEncoladosMinus();
                double ta = getTA();
                ta = ta * 0.7;
                updateSeniorTps(var, index, ta);
                var.getResultado().addStaC(ta);
            }
        }

    }

    private static void procesarSalidaJunior(Variables var, Integer index) {
        LocalDateTime tAux = var.getT();
        LocalDateTime tpsAux = var.getEstado().getTpsJr()[var.getI()];

        var.setT(var.getEstado().getTpsJr()[var.getI()]);
        var.getResultado().setCrJr(var.getResultado().getCrJr() + 1);

        String ca = var.getEstado().getJuniorAtendiendo()[index];

        if (ca.equalsIgnoreCase("M")) {
            var.getResultado().addStsM(tpsAux, tAux, var.getEstado().getCtm());
            var.getEstado().setCtm(var.getEstado().getCtm() - 1);
        } else if (ca.equalsIgnoreCase("B")){
            var.getResultado().addStsB(tpsAux, tAux, var.getEstado().getCtb());
            var.getEstado().setCtb(var.getEstado().getCtb() - 1);
        }

        var.getEstado().getJuniorAtendiendo()[index] = "";

        if (var.getEstado().getMediosEncolados() >= 1) {
            var.getEstado().getJuniorAtendiendo()[index] = "M";
            var.getEstado().mediosEncoladosMinus();
            double ta = getTA();
            ta = ta * 1.5;
            updateJuniorTps(var, var.getI(), ta);
            var.getResultado().addStaM(ta);
        } else if (var.getEstado().getBajosEncolados() >= 1) {
            var.getEstado().getJuniorAtendiendo()[index] = "B";
            var.getEstado().bajosEncoladosMinus();
            double ta = getTA();
            ta = ta * 1.5;
            updateJuniorTps(var, var.getI(), ta);
            var.getResultado().addStaB(ta);
        } else {
            var.getEstado().getTpsJr()[var.getI()] = null;
        }
    }

    private static void procesarSalidaSemiSenior(Variables var, Integer index) {
        LocalDateTime tAux = var.getT();
        LocalDateTime tpsAux = var.getEstado().getTpsSsr()[var.getJ()];

        var.setT(var.getEstado().getTpsSsr()[var.getJ()]);
        var.getResultado().setCrSsr(var.getResultado().getCrSsr() + 1);

        String ca = var.getEstado().getSemiSeniorAtendiendo()[index];
        var.getEstado().getSemiSeniorAtendiendo()[index] = "";

        if (ca.equalsIgnoreCase("A")) {
            var.getResultado().addStsA(tpsAux, tAux, var.getEstado().getCta());
            var.getEstado().setCta(var.getEstado().getCta() - 1);
        } else if (ca.equalsIgnoreCase("M")) {
            var.getResultado().addStsM(tpsAux, tAux, var.getEstado().getCtm());
            var.getEstado().setCtm(var.getEstado().getCtm() - 1);
        } else if (ca.equalsIgnoreCase("B")) {
            var.getResultado().addStsB(tpsAux, tAux, var.getEstado().getCtb());
            var.getEstado().setCtb(var.getEstado().getCtb() - 1);
        }


        if (var.getEstado().getAltosEncolados() >= 1) {
            var.getEstado().getSemiSeniorAtendiendo()[index] = "A";
            var.getEstado().altosEncoladosMinus();
            double ta = getTA();
            updateSemiSeniorTps(var, var.getJ(), ta);
            var.getResultado().addStaA(ta);
        } else if (var.getEstado().getMediosEncolados() >= 1) {
            var.getEstado().getSemiSeniorAtendiendo()[index] = "M";
            var.getEstado().mediosEncoladosMinus();
            double ta = getTA();
            updateSemiSeniorTps(var, var.getJ(), ta);
            var.getResultado().addStaM(ta);
        } else if (var.getEstado().getBajosEncolados() >= 1) {
            var.getEstado().getSemiSeniorAtendiendo()[index] = "B";
            var.getEstado().bajosEncoladosMinus();
            double ta = getTA();
            updateSemiSeniorTps(var, var.getJ(), ta);
            var.getResultado().addStaB(ta);
        } else {
            var.getEstado().getTpsSsr()[var.getJ()] = null;
        }
    }

    private static void procesarSalidaSenior(Variables var, Integer index) {
        LocalDateTime tAux = var.getT();
        LocalDateTime tpsAux = var.getEstado().getTpsSr()[var.getK()];

        var.setT(var.getEstado().getTpsSr()[var.getK()]);
        var.getResultado().setCrSr(var.getResultado().getCrSr() + 1);

        String ca = var.getEstado().getSeniorAtendiendo()[index];
        var.getEstado().getSeniorAtendiendo()[index] = "";

        if (ca.equalsIgnoreCase("C")) {
            var.getResultado().addStsC(tpsAux, tAux, var.getEstado().getCtc());
            var.getEstado().setCtc(var.getEstado().getCtc() - 1);
        } else if (ca.equalsIgnoreCase("A")) {
            var.getResultado().addStsA(tpsAux, tAux, var.getEstado().getCta());
            var.getEstado().setCta(var.getEstado().getCta() - 1);
        } else if (ca.equalsIgnoreCase("M")) {
            var.getResultado().addStsM(tpsAux, tAux, var.getEstado().getCtm());
            var.getEstado().setCtm(var.getEstado().getCtm() - 1);
        } else {
            var.getResultado().setCrSr(var.getResultado().getCrSr() + 1);
            var.getResultado().addStsB(tpsAux, tAux, var.getEstado().getCtb());
            var.getEstado().setCtb(var.getEstado().getCtb() - 2);
        }


        if (var.getEstado().getCriticosEncolados() >= 1) {
            var.getEstado().getSeniorAtendiendo()[index] = "C";
            var.getEstado().criticosEncoladosMinus();
            double ta = getTA();
            ta = ta * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaC(ta);
        } else if (var.getEstado().getAltosEncolados() >= 1) {
            var.getEstado().getSeniorAtendiendo()[index] = "A";
            var.getEstado().altosEncoladosMinus();
            double ta = getTA();
            ta = ta * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaA(ta);
        } else if (var.getEstado().getMediosEncolados() >= 1) {
            var.getEstado().getSeniorAtendiendo()[index] = "M";
            var.getEstado().mediosEncoladosMinus();
            double ta = getTA();
            ta = ta * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaM(ta);
        } else if (var.getEstado().getBajosEncolados() >= 2) {
            var.getEstado().getSeniorAtendiendo()[index] = "B";
            var.getEstado().bajosEncoladosMinus();
            var.getEstado().bajosEncoladosMinus();
            double ta = getTA();
            double ta2 = getTA();
            ta = (ta + ta2) * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaB(ta);
        } else {
            var.getEstado().getTpsSr()[var.getK()] = null;
        }
    }

}
