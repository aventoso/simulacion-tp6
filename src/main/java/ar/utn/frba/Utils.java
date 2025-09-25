package ar.utn.frba;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

public class Utils {

    /*
     * getTa() -> double (min)
     * */
    public static Double getTA() {
        // Parámetros ajustados
        double df = 0.5518296905342877;
        double loc = -3.3265091015624795e-26;
        double scale = 13290.042848969457;

        // Distribución Chi²
        ChiSquaredDistribution chi = new ChiSquaredDistribution(df);

        // Aleatorio uniforme entre 0 y 0.665748
        double r = Math.random() * 0.665748;

        // Inversa de la CDF

        return chi.inverseCumulativeProbability(r) * scale + loc;
    }

    /*
     * getIa() -> double (min)
     * */
    public static Double getIA() {
        // Parámetros ajustados
        double c = 0.9832892843344954;
        double loc_param = -1.9046713314091517e-27;
        double scale_param = 46.01390465991299;

        // Distribución Fatigue Life
        WeibullDistribution dist = new WeibullDistribution(c, scale_param);

        // Generar un valor aleatorio en (0,1)
        Random rand = new Random();
        double r = rand.nextDouble();

        // Usar la inversa (ppf en Python → inverseCumulativeProbability en Commons Math)
        return dist.inverseCumulativeProbability(r) + loc_param;
    }

    public static int getMenorTps(LocalDateTime[] tps) {
        int indice = 0;
        for (int i = 0; i < tps.length; i++) {
            if (tps[indice] != null) {
                if (tps[i] == null || tps[i].isBefore(tps[indice])) {
                    indice = i;
                }
            }
        }
        return indice;
    }

    public static Integer getMenorTpsIgnorandoNulls(LocalDateTime[] tps) {
        Integer indiceMenor = null;

        for (int i = 0; i < tps.length; i++) {
            if (tps[i] != null) {
                if (indiceMenor == null || tps[i].isBefore(tps[indiceMenor])) {
                    indiceMenor = i;
                }
            }
        }

        return indiceMenor; // si no encontró ninguno, retorna null
    }

    public static Integer getSeniorDisponible(Variables var) {
        return getPuestoLibre(var.getEstado().getTpsSr());
    }

    public static Integer getSemiSeniorDisponible(Variables var) {
        return getPuestoLibre(var.getEstado().getTpsSsr());
    }

    public static Integer getJuniorDisponible(Variables var) {
        return getPuestoLibre(var.getEstado().getTpsJr());
    }

    public static void updateJuniorTps(Variables var, int index, double value) {
        updateTps(var.getEstado().getTpsJr(), index, value, var.getT());
    }

    public static void updateSemiSeniorTps(Variables var, int index, double value) {
        updateTps(var.getEstado().getTpsSsr(), index, value, var.getT());
    }

    public static void updateSeniorTps(Variables var, int index, double value) {
        updateTps(var.getEstado().getTpsSr(), index, value, var.getT());
    }


    private static void updateTps(LocalDateTime[] tps, int index, double value, LocalDateTime t) {
        if (tps[index] == null) {
            tps[index] = t.plusMinutes(Math.round(value));
        } else {
            tps[index] = tps[index].plusMinutes(Math.round(value));
        }
    }

    private static Integer getPuestoLibre(LocalDateTime[] tps) {
        Integer index = null;
        for (int i = 0; i < tps.length; i++) {
            LocalDateTime tp = tps[i];
            if (Objects.isNull(tp)) {
                index = i;
            }
        }
        return index;
    }
}
