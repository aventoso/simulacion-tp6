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

        LOGGER.info("La simulación arranca el día 01/01/2025 a las 9:00 hrs");
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
                if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) && Objects.nonNull(variables.getEstado().getTpsSr()[k]) &&
                    (variables.getEstado().getTpsJr()[i].isBefore(variables.getEstado().getTpsSr()[k]) ||
                     variables.getEstado().getTpsJr()[i].equals(variables.getEstado().getTpsSr()[k]))) {
                    if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) &&
                        (variables.getEstado().getTpsJr()[i].isBefore(variables.getTpll()) ||
                         variables.getEstado().getTpsJr()[i].equals(variables.getTpll()))) {
                        // Salida Junion
                        procesarSalidaJunior(variables);
                    } else {
                        procesarLlegada(variables);
                    }
                } else if (Objects.nonNull(variables.getEstado().getTpsSsr()[j]) &&
                           (variables.getEstado().getTpsSsr()[j].isBefore(variables.getTpll()) ||
                            variables.getEstado().getTpsSsr()[j].equals(variables.getTpll()))) {
                    // salida ssr
                    procesarSalidaSemiSenior(variables);
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
                    procesarSalidaSemiSenior(variables);
                } else {
                    procesarLlegada(variables);
                }
            } else if (Objects.nonNull(variables.getEstado().getTpsSr()[k]) &&
                       (variables.getEstado().getTpsSr()[k].isBefore(variables.getTpll()) ||
                        variables.getEstado().getTpsSr()[k].equals(variables.getTpll()))) {
                // salida senior
                procesarSalidaSenior(variables);
            } else {
                // llegada
                procesarLlegada(variables);
            }

            LOGGER.info(variables.getEstado().toString());

        }

        // Resultados finales (placeholders porque en tu código Python estaban incompletos)
        LOGGER.info("\n=== Resultados ===");
        LOGGER.info("Iteraciones: {}",iteraciones);
        LOGGER.info("PTB: {}", (variables.getResultado().getCttb() /  variables.getResultado().getCtt()) * 100);
        LOGGER.info("PTM: {}", (variables.getResultado().getCttm() /  variables.getResultado().getCtt()) * 100);
        LOGGER.info("PTA: {}", (variables.getResultado().getCtta() /  variables.getResultado().getCtt()) * 100);
        LOGGER.info("PTC: {}", (variables.getResultado().getCttc() /  variables.getResultado().getCtt()) * 100);


        scanner.close();
    }

    private static void procesarLlegada(Variables var) {
        LocalDateTime tAux = var.getT();
        LocalDateTime tpllAux = var.getTpll();
        var.getResultado().setCtt(var.getResultado().getCtt() + 1);

        var.setT(var.getTpll());
        double ia = getIA();
        var.setTpll(var.getT().plusSeconds(Math.round(ia * 60.0)));
        double r = Math.random();

        var.setCantidadTotalTickets(var.getCantidadTotalTickets() + 1);

        if (r < 0.2) {
            //criticidad baja
            //var.getResultado().setStsB()
            var.getEstado().bPlus();
            var.getResultado().setCttb(var.getResultado().getCttb() + 1);

            Integer indexJunior = getJuniorDisponible(var);

            if (Objects.nonNull(indexJunior)) {
                var.getEstado().bMinus();
                double ta = getTA();
                ta = ta * 1.5;
                updateJuniorTps(var, indexJunior, ta);
                var.getResultado().addStaB(ta);
                var.getResultado().setCrJr(var.getResultado().getCrJr() + 1);
            } else {
                Integer indexSemiSenior = getJuniorDisponible(var);
                if (Objects.nonNull(indexSemiSenior)) {
                    var.getEstado().bMinus();
                    double ta = getTA();
                    updateSemiSeniorTps(var, indexSemiSenior, ta);
                    var.getResultado().addStaB(ta);
                    var.getResultado().setCrSsr(var.getResultado().getCrSsr() + 1);
                } else {
                    Integer indexSenior = getSeniorDisponible(var);
                    if (Objects.nonNull(indexSenior) && var.getEstado().getBajosEncolados() >= 2) {
                        var.getEstado().bMinus();
                        var.getEstado().bMinus();
                        double ta = getTA();
                        double ta2 = getTA();
                        double tat = (ta + ta2) * 1.4;
                        updateSeniorTps(var, indexSenior, tat);
                        var.getResultado().addStaB(tat);
                        var.getResultado().setCrSr(var.getResultado().getCrSr() + 1);

                    }

                }
            }
        } else if (r < 0.5) {
            //medios
            var.getEstado().mPlus();
            var.getResultado().setCttm(var.getResultado().getCttm() + 1);


            Integer indexJunior = getJuniorDisponible(var);

            if (Objects.nonNull(indexJunior)) {
                var.getEstado().mMinus();
                double ta = getTA();
                ta = ta * 1.5;
                updateJuniorTps(var, indexJunior, ta);
                var.getResultado().addStaM(ta);
                var.getResultado().setCrJr(var.getResultado().getCrJr() + 1);
            } else {
                Integer indexSemiSenior = getSemiSeniorDisponible(var);
                if (Objects.nonNull(indexSemiSenior)) {
                    var.getEstado().mMinus();
                    double ta = getTA();
                    updateSemiSeniorTps(var, indexSemiSenior, ta);
                    var.getResultado().addStaM(ta);
                    var.getResultado().setCrSsr(var.getResultado().getCrSsr() + 1);
                } else {
                    Integer indexSenior = getSeniorDisponible(var);
                    if (Objects.nonNull(indexSenior)) {
                        var.getEstado().mMinus();
                        double ta = getTA();
                        ta = ta * 0.7;
                        updateSeniorTps(var, indexSenior, ta);
                        var.getResultado().addStaM(ta);
                        var.getResultado().setCrSr(var.getResultado().getCrSr() + 1);

                    }

                }
            }
        } else if (r < 0.8) {
            // llega alto
            var.getEstado().aPlus();
            var.getResultado().setCtta(var.getResultado().getCtta() + 1);


            Integer indexSSenior = getSemiSeniorDisponible(var);

            if (Objects.nonNull(indexSSenior)) {
                var.getEstado().aMinus();
                double ta = getTA();
                updateSemiSeniorTps(var, indexSSenior, ta);
                var.getResultado().addStaA(ta);
                var.getResultado().setCrSsr(var.getResultado().getCrSsr() + 1);
            } else {
                Integer indexSenior = getSeniorDisponible(var);
                if (Objects.nonNull(indexSenior)) {
                    var.getEstado().aMinus();
                    double ta = getTA();
                    ta = ta * 0.7;
                    updateSeniorTps(var, indexSenior, ta);
                    var.getResultado().addStaA(ta);
                    var.getResultado().setCrSr(var.getResultado().getCrSr() + 1);
                }
            }
        } else {
            //llega critico
            var.getEstado().cPlus();
            var.getResultado().setCttc(var.getResultado().getCttc() + 1);

            Integer index = getSeniorDisponible(var);

            if (Objects.nonNull(index)) {
                var.getEstado().cMinus();
                double ta = getTA();
                ta = ta * 0.7;
                updateSeniorTps(var, index, ta);
                var.getResultado().addStaC(ta);
                var.getResultado().setCrSr(var.getResultado().getCrSr() + 1);
            }
        }

    }

    private static void procesarSalidaJunior(Variables var) {
        var.setT(var.getEstado().getTpsJr()[var.getI()]);
        if (var.getEstado().getMediosEncolados() >= 1) {
            var.getEstado().mMinus();
            double ta = getTA();
            ta = ta * 1.5;
            updateJuniorTps(var, var.getI(), ta);
            var.getResultado().addStaM(ta);
        } else if (var.getEstado().getBajosEncolados() >= 1) {
            var.getEstado().bMinus();
            double ta = getTA();
            ta = ta * 1.5;
            updateJuniorTps(var, var.getI(), ta);
            var.getResultado().addStaB(ta);
        } else {
            var.getEstado().getTpsJr()[var.getI()] = null;
        }
    }

    private static void procesarSalidaSemiSenior(Variables var) {
        var.setT(var.getEstado().getTpsSsr()[var.getJ()]);
        if (var.getEstado().getAltosEncolados() >= 1) {
            var.getEstado().aMinus();
            double ta = getTA();
            updateSemiSeniorTps(var, var.getJ(), ta);
            var.getResultado().addStaA(ta);
        } else if (var.getEstado().getMediosEncolados() >= 1) {
            var.getEstado().mMinus();
            double ta = getTA();
            updateSemiSeniorTps(var, var.getJ(), ta);
            var.getResultado().addStaM(ta);
        } else if (var.getEstado().getBajosEncolados() >= 1) {
            var.getEstado().bMinus();
            double ta = getTA();
            updateSemiSeniorTps(var, var.getJ(), ta);
            var.getResultado().addStaB(ta);
        } else {
            var.getEstado().getTpsSsr()[var.getJ()] = null;
        }
    }

    private static void procesarSalidaSenior(Variables var) {
        var.setT(var.getEstado().getTpsSr()[var.getK()]);
        if (var.getEstado().getCriticosEncolados() >= 1) {
            var.getEstado().cMinus();
            double ta = getTA();
            ta = ta * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaC(ta);
        } else if (var.getEstado().getAltosEncolados() >= 1) {
            var.getEstado().aMinus();
            double ta = getTA();
            ta = ta * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaA(ta);
        } else if (var.getEstado().getMediosEncolados() >= 1) {
            var.getEstado().mMinus();
            double ta = getTA();
            ta = ta * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaM(ta);
        } else if (var.getEstado().getBajosEncolados() >= 1) {
            var.getEstado().bMinus();
            double ta = getTA();
            ta = ta * 0.7;
            updateSeniorTps(var, var.getK(), ta);
            var.getResultado().addStaB(ta);
        } else {
            var.getEstado().getTpsSr()[var.getK()] = null;
        }
    }

}
