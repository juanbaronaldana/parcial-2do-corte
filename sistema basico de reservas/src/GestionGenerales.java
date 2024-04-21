import java.util.Date;
import java.util.List;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class GestionGenerales implements GestiónDeReservas {
    private static final String ARCHIVO_RESERVAS = "reservas.txt";
    private static final String ARCHIVO_HABITACIONES = "habitaciones.txt";

    @Override
    public void reservarHabitacion(Usuario usuario, Habitacion habitacion, Date fechaInicio, Date fechaFin) {
        try (FileWriter writer = new FileWriter(ARCHIVO_RESERVAS, true)) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            writer.write(usuario.getUsername() + "," + habitacion.getTipo() + "," + formatter.format(fechaInicio) + "," + formatter.format(fechaFin) + "\n");
            System.out.println("Reserva realizada para el usuario " + usuario.getUsername() + " en la habitación " + habitacion.getTipo());
        } catch (IOException e) {
            System.out.println("Error al guardar la reserva.");
            e.printStackTrace();
        }
    }

    public void cancelarReserva(Usuario usuario, Reserva reserva) {
        List<Reserva> reservas = cargarReservas();
        boolean reservaEncontrada = false;
        for (Reserva r : reservas) {
            if (r.getUsuario().getUsername().equals(usuario.getUsername()) && r.getHabitacion().getTipo().equals(reserva.getHabitacion().getTipo())
                && r.getFechaInicio().equals(reserva.getFechaInicio()) && r.getFechaFin().equals(reserva.getFechaFin())) {
                reservas.remove(r);
                reservaEncontrada = true;
                break;
            }
        }
        if (reservaEncontrada) {
            try (PrintWriter writer = new PrintWriter(ARCHIVO_RESERVAS)) {
                for (Reserva r : reservas) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    writer.println(r.getUsuario().getUsername() + "," + r.getHabitacion().getTipo() + "," + formatter.format(r.getFechaInicio()) + "," + formatter.format(r.getFechaFin()));
                }
                System.out.println("Reserva cancelada para el usuario " + usuario.getUsername() + " en la habitación " + reserva.getHabitacion().getTipo());
            } catch (IOException e) {
                System.out.println("Error al cancelar la reserva.");
                e.printStackTrace();
            }
        } else {
            System.out.println("No se encontró la reserva para cancelar.");
        }
    }

    @Override
    public List<Habitacion> habitacionesDisponibles(Date fechaInicio, Date fechaFin) {
        List<Habitacion> habitacionesDisponibles = new ArrayList<>();
        List<Reserva> reservas = cargarReservas();
        List<Habitacion> todasLasHabitaciones = cargarHabitaciones();
        for (Habitacion habitacion : todasLasHabitaciones) {
            boolean disponible = true;
            for (Reserva reserva : reservas) {
                if (reserva.getHabitacion().equals(habitacion) &&
                    !(fechaInicio.after(reserva.getFechaFin()) || fechaFin.before(reserva.getFechaInicio()))) {
                    disponible = false;
                    break;
                }
            }
            if (disponible) {
                habitacionesDisponibles.add(habitacion);
            }
        }
        return habitacionesDisponibles;
    }

    private List<Reserva> cargarReservas() {
        List<Reserva> reservas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_RESERVAS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                Usuario usuario = new Usuario(partes[0], ""); 
                Habitacion habitacion = new Habitacion(partes[1], 0.0f, 0, ""); 
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date fechaInicio = formatter.parse(partes[2]);
                Date fechaFin = formatter.parse(partes[3]);
                reservas.add(new Reserva(usuario, habitacion, fechaInicio, fechaFin));
            }
        } catch (Exception e) {
            System.out.println("Error al cargar las reservas.");
            e.printStackTrace();
        }
        return reservas;
    }

    @Override
    public void eliminarHabitacion(String tipoHabitacionEliminar) {
        List<Habitacion> habitaciones = cargarHabitaciones();
        try (FileWriter writer = new FileWriter(ARCHIVO_HABITACIONES)) {
            for (Habitacion habitacion : habitaciones) {
                if (!habitacion.getTipo().equals(tipoHabitacionEliminar)) {
                    writer.write(habitacion.getTipo() + "," + habitacion.getPrecioPorNoche() + "," + habitacion.getNumMaxHuespedes() + "," + habitacion.getComodidades() + "\n");
                }
            }
            System.out.println("Habitación eliminada exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al eliminar la habitación.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Habitacion> cargarHabitaciones() {
        List<Habitacion> habitaciones = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_HABITACIONES))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                String tipo = partes[0];
                float precioPorNoche = Float.parseFloat(partes[1]);
                int numMaxHuespedes = Integer.parseInt(partes[2]);
                String comodidades = partes[3];
                habitaciones.add(new Habitacion(tipo, precioPorNoche, numMaxHuespedes, comodidades));
            }
        } catch (IOException e) {
            System.out.println("Error al cargar las habitaciones.");
            e.printStackTrace();
        }
        return habitaciones;
    }

    @Override
    public void guardarHabitaciones(List<Habitacion> habitaciones) {
        try (FileWriter writer = new FileWriter(ARCHIVO_HABITACIONES)) {
            for (Habitacion habitacion : habitaciones) {
                writer.write(habitacion.getTipo() + "," + habitacion.getPrecioPorNoche() + "," + habitacion.getNumMaxHuespedes() + "," + habitacion.getComodidades() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error al guardar las habitaciones.");
            e.printStackTrace();
        }
    }
}
