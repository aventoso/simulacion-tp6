package ar.utn.frba;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

import static ar.utn.frba.Utils.*;


public class Main {


    public static void main(String[] args) {
        System.out.println("=== Simulación: Cuantos desarrolladores se necesitan para solucionar hotfix ===");

        Scanner scanner = new Scanner(System.in);

        System.out.println("La simulación arranca el día 01/01/2025 a las 9:00 hrs");
        System.out.print("Ingrese el tiempo de finalización (formato: 2025/04/01 19:30): ");
        String tiempoFinalStr = "2025/04/01 19:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime tiempoFinal = LocalDateTime.parse(tiempoFinalStr, formatter);

        System.out.print("Cantidad de desarrolladores junior: ");
        int nj = scanner.nextInt();

        System.out.print("Cantidad de desarrolladores semi-senior: ");
        int nss = scanner.nextInt();

        System.out.print("Cantidad de desarrolladores senior: ");
        int ns = scanner.nextInt();

        // Variables de estado


        Variables.Estado estado = new Variables.Estado(nj, nss, ns);
        Variables.Control control = new Variables.Control(nj, nss, ns);


        Variables variables = new Variables(estado, control);


        // Loop principal
        while (variables.getT().isBefore(tiempoFinal)) {
            int i = getMenorTps(variables.getEstado().getTpsJr());
            int j = getMenorTps(variables.getEstado().getTpsSsr());
            int k = getMenorTps(variables.getEstado().getTpsSr());

            variables.setI(i);
            variables.setJ(j);
            variables.setK(k);

            if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) &&
                (variables.getEstado().getTpsJr()[i].isBefore(variables.getEstado().getTpsSr()[j]) ||
                 variables.getEstado().getTpsJr()[i].equals(variables.getEstado().getTpsSr()[j]))) {
                if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) &&
                    (variables.getEstado().getTpsJr()[i].isBefore(variables.getEstado().getTpsSr()[k]) ||
                     variables.getEstado().getTpsJr()[i].equals(variables.getEstado().getTpsSr()[k]))) {
                    if (Objects.nonNull(variables.getEstado().getTpsJr()[i]) &&
                        (variables.getEstado().getTpsJr()[i].isBefore(variables.getTpll().toLocalTime()))) {
                        // Salida Junion
                        procesarSalidaJunior(variables);
                    } else {
                        procesarLlegada(variables);
                    }
                } else if (Objects.nonNull(variables.getEstado().getTpsSr()[j]) &&
                           (variables.getEstado().getTpsSr()[j].isBefore(variables.getTpll().toLocalTime()) ||
                            variables.getEstado().getTpsSr()[j].equals(variables.getTpll().toLocalTime()))) {
                    // salida ssr
                    procesarSalidaSemiSenior(variables);
                } else {
                    procesarLlegada(variables);
                }

            } else if (Objects.nonNull(variables.getEstado().getTpsSr()[j]) &&
                       (variables.getEstado().getTpsSr()[j].isBefore(variables.getEstado().getTpsSr()[k]) ||
                        variables.getEstado().getTpsSr()[k].equals(variables.getEstado().getTpsSr()[k]))) {
                if (Objects.nonNull(variables.getEstado().getTpsSr()[j]) &&
                    (variables.getEstado().getTpsSr()[j].isBefore(variables.getTpll().toLocalTime()))) {
                    // salida ssr
                    procesarSalidaSemiSenior(variables);
                } else {
                    procesarLlegada(variables);
                }
            } else if (Objects.nonNull(variables.getEstado().getTpsSr()[k]) &&
                       (variables.getEstado().getTpsSr()[k].isBefore(variables.getTpll().toLocalTime()) ||
                        variables.getEstado().getTpsSr()[k].equals(variables.getTpll().toLocalTime()))) {
                // salida senior
            } else {
                // llegada
                procesarLlegada(variables);
            }


        }

        // Resultados finales (placeholders porque en tu código Python estaban incompletos)
        System.out.println("\n=== Resultados ===");
        System.out.println("Iteraciones: (faltante)");
        System.out.println("Tasa de llegada: (faltante)");
        System.out.println("Capacidad: (faltante)");
        System.out.println("Estado final: (faltante)");
        System.out.println("Promedio de ocupación: (faltante)");

        scanner.close();
    }

    private static void procesarLlegada(Variables var) {
        var.setT(var.getTpll());
        double ia = getIA();
        var.setTpll(var.getT().plusSeconds(Math.round(ia * 60.0)));
        double r = Math.random();

        if (r < 0.2) {
            var.getEstado().bPlus();
            var.getEstado().bePlus();

            Integer indexSenior = getSeniorDisponible(
                    var); // revisar, en reliadad creo qye sobra variable. Actualizo tranto seniorAtendiendo como tps
            Integer indexSSenior = getSemiSeniorDisponible(var);
            Integer indexJunior = getJuniorDisponible(var);

            if (Objects.nonNull(indexSenior) && var.getEstado().getBajosEncolados() >= 2) {
                var.getEstado().getSeniorAtendiendo()[indexSenior] = "M";
                double ta = getTA();
                ta = ta * 0.7;
                double ta2 = getTA();
                ta2 = ta2 * 0.7;
                updateSeniorTps(var, indexSenior, ta + ta2);
            } else if (Objects.nonNull(indexSSenior)) {
                var.getEstado().getSseniorAtendiendo()[indexSSenior] = "M";
                double ta = getTA();
                updateSemiSeniorTps(var, indexSSenior, ta);

            } else if (Objects.nonNull(indexJunior)) {
                var.getEstado().getJuniorAtendiendo()[indexJunior] = "M";
                double ta = getTA();
                ta = ta * 1.5;
                updateJuniorTps(var, indexJunior, ta);
            }
        } else if (r < 0.5) {
            // llega medio
            var.getEstado().mPlus();
            Integer indexSenior = getSeniorDisponible(var);
            Integer indexSSenior = getSemiSeniorDisponible(var);
            Integer indexJunior = getJuniorDisponible(var);
            if (Objects.nonNull(indexSenior)) {
                var.getEstado().getSeniorAtendiendo()[indexSenior] = "M";
                double ta = getTA();
                ta = ta * 0.7;
                updateSeniorTps(var, indexSenior, ta);
            } else if (Objects.nonNull(indexSSenior)) {
                var.getEstado().getSseniorAtendiendo()[indexSSenior] = "M";
                double ta = getTA();
                updateSemiSeniorTps(var, indexSSenior, ta);

            } else if (Objects.nonNull(indexJunior)) {
                var.getEstado().getJuniorAtendiendo()[indexJunior] = "M";
                double ta = getTA();
                ta = ta * 1.5;
                updateJuniorTps(var, indexJunior, ta);

            }
        } else if (r < 0.8) {
            // llega alto
            var.getEstado().aPlus();
            Integer indexSenior = getSeniorDisponible(var);
            Integer indexSSenior = getSemiSeniorDisponible(var);
            if (Objects.nonNull(indexSenior)) {
                var.getEstado().getSeniorAtendiendo()[indexSenior] = "A";
                double ta = getTA();
                ta = ta * 0.7;
                updateSeniorTps(var, indexSenior, ta);
            } else if (Objects.nonNull(indexSSenior)) {
                var.getEstado().getSseniorAtendiendo()[indexSSenior] = "A";
                double ta = getTA();
                updateSemiSeniorTps(var, indexSSenior, ta);

            }

        } else {
            //llega critico
            var.getEstado().cPlus();
            Integer index = getSeniorDisponible(var);

            if (Objects.nonNull(index)) {
                var.getEstado().getSeniorAtendiendo()[index] = "C";
                double ta = getTA();
                ta = ta * 0.7;
                updateSeniorTps(var, index, ta);
            }
        }

    }

    private static void procesarSalidaJunior(Variables var) {
        var.setT(LocalDateTime.from(var.getEstado().getTpsJr()[var.getI()]));
        if (var.getEstado().getCantidadMedios() > var.getEstado().getNss() + var.getEstado().getNs()) {
            var.getEstado().mMinus();
        } else if (var.getEstado().getCantidadBajos() > var.getEstado().getNss() + var.getEstado().getNs()) {
            var.getEstado().bMinus();
        } else {
            var.getEstado().getTpsJr()[var.getI()] = null;
            return;
        }
        double ta = getTA();
        ta = ta * 1.5;
        var.getEstado().getTpsJr()[var.getI()] =
                LocalTime.from(var.getT().plusSeconds(Long.parseLong(String.valueOf(ta))));
    }

    private static void procesarSalidaSemiSenior(Variables var) {
        var.setT(LocalDateTime.from(var.getEstado().getTpsSsr()[var.getJ()]));
        if (var.getEstado().getCantidadAltos() > var.getEstado().getNs()) {
            var.getEstado().aMinus();
        } else if (var.getEstado().getCantidadMedios() > var.getEstado().getNss() + var.getEstado().getNs()) {
            var.getEstado().mMinus();
        } else if (var.getEstado().getCantidadBajos() > var.getEstado().getNss() + var.getEstado().getNs()) {
            var.getEstado().bMinus();
        } else {
            var.getEstado().getTpsSsr()[var.getJ()] = null;
            return;
        }
        double ta = getTA();
        var.getEstado().getTpsSsr()[var.getJ()] =
                LocalTime.from(var.getT().plusSeconds(Long.parseLong(String.valueOf(ta))));
    }

}
