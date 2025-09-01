package com.login.views;

import com.login.Main;
import com.login.model.*;
import com.login.repository.CursosInscritos;
import com.login.repository.CursosProfesores;
import com.login.repository.InscripcionesPersonas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class InterfazGrafica extends JFrame {

    private final InscripcionesPersonas personas;
    private final CursosProfesores cursosProfesores;
    private final CursosInscritos cursosInscritos;
    private final List<Curso> listaCursos;

    private final JTextArea areaSalida;

    public InterfazGrafica(InscripcionesPersonas personas,
                           CursosProfesores cursosProfesores,
                           CursosInscritos cursosInscritos,
                           List<Curso> listaCursos) {
        this.personas = personas;
        this.cursosProfesores = cursosProfesores;
        this.cursosInscritos = cursosInscritos;
        this.listaCursos = listaCursos;

        this.areaSalida = new JTextArea(15, 50);
        this.areaSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaSalida);

        setTitle("Sistema Universitario - GUI");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ⚠️ No cerrar sin confirmar
        setLayout(new BorderLayout());

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 5, 5));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnPersonas = new JButton("Registrar Persona");
        JButton btnInscribir = new JButton("Inscribir Estudiante a Curso");
        JButton btnAsignar = new JButton("Asignar Curso a Profesor");
        JButton btnListados = new JButton("Mostrar Listados");
        JButton btnSalir = new JButton("Salir");

        panelBotones.add(btnPersonas);
        panelBotones.add(btnInscribir);
        panelBotones.add(btnAsignar);
        panelBotones.add(btnListados);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        // Acciones
        btnPersonas.addActionListener(e -> abrirVentanaRegistrarPersona());
        btnInscribir.addActionListener(e -> abrirVentanaInscribirEstudiante());
        btnAsignar.addActionListener(e -> abrirVentanaAsignarCursoProfesor());
        btnListados.addActionListener(e -> mostrarListados());
        btnSalir.addActionListener(e -> salir());

        // Confirmar cierre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ✅ Ventana personalizada: Registrar Persona
    private void abrirVentanaRegistrarPersona() {
        JDialog dialog = new JDialog(this, "Registrar Persona", true); // Modal
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField(15);
        JTextField nombreField = new JTextField(15);
        JTextField apellidoField = new JTextField(15);
        JTextField emailField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; dialog.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 1; dialog.add(nombreField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1; dialog.add(apellidoField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; dialog.add(emailField, gbc);

        JPanel botones = new JPanel();
        JButton guardar = new JButton("Guardar");
        JButton cancelar = new JButton("Cancelar");

        guardar.addActionListener(e -> {
            try {
                double id = Double.parseDouble(idField.getText().trim());
                String nombres = nombreField.getText().trim();
                String apellidos = apellidoField.getText().trim();
                String email = emailField.getText().trim();

                if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Persona persona = new Persona(id, nombres, apellidos, email);
                personas.inscribir(persona);

                JOptionPane.showMessageDialog(dialog, "✅ Persona registrada con éxito.");
                appendOutput("Persona registrada: " + persona.toString());
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(e -> dialog.dispose());

        botones.add(guardar);
        botones.add(cancelar);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(botones, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ✅ Ventana personalizada: Inscribir Estudiante
    private void abrirVentanaInscribirEstudiante() {
        JDialog dialog = new JDialog(this, "Inscribir Estudiante", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Persona> listaPersonas = personas.getListado();
        List<Curso> cursosDisponibles = cursosProfesores.getCursos();

        if (listaPersonas.isEmpty() || cursosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay personas o cursos disponibles.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Persona> comboPersonas = new JComboBox<>(listaPersonas.toArray(new Persona[0]));
        JComboBox<Curso> comboCursos = new JComboBox<>(cursosDisponibles.toArray(new Curso[0]));
        JTextField añoField = new JTextField("2024", 10);
        JTextField semestreField = new JTextField("1", 10);

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1; dialog.add(comboPersonas, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Curso:"), gbc);
        gbc.gridx = 1; dialog.add(comboCursos, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Año:"), gbc);
        gbc.gridx = 1; dialog.add(añoField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Semestre:"), gbc);
        gbc.gridx = 1; dialog.add(semestreField, gbc);

        JPanel botones = new JPanel();
        JButton guardar = new JButton("Guardar");
        JButton cancelar = new JButton("Cancelar");

        guardar.addActionListener(e -> {
            try {
                Persona seleccionada = (Persona) comboPersonas.getSelectedItem();
                Curso curso = (Curso) comboCursos.getSelectedItem();
                int año = Integer.parseInt(añoField.getText().trim());
                int semestre = Integer.parseInt(semestreField.getText().trim());

                Estudiante estudiante = (seleccionada instanceof Estudiante)
                        ? (Estudiante) seleccionada
                        : Main.crearEstudianteDesdePersona(seleccionada);

                Inscripcion inscripcion = new Inscripcion(curso, año, semestre, estudiante);
                cursosInscritos.guardarInformacion(inscripcion);

                JOptionPane.showMessageDialog(dialog, "✅ Estudiante inscrito con éxito.");
                appendOutput("Inscripción: " + inscripcion.toString());
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Año y semestre deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(e -> dialog.dispose());

        botones.add(guardar);
        botones.add(cancelar);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(botones, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ✅ Ventana personalizada: Asignar Curso a Profesor
    private void abrirVentanaAsignarCursoProfesor() {
        JDialog dialog = new JDialog(this, "Asignar Curso a Profesor", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Persona> listaPersonas = personas.getListado();
        List<Curso> cursosDisponibles = cursosProfesores.getCursos();

        if (listaPersonas.isEmpty() || cursosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay personas o cursos disponibles.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Persona> comboPersonas = new JComboBox<>(listaPersonas.toArray(new Persona[0]));
        JComboBox<Curso> comboCursos = new JComboBox<>(cursosDisponibles.toArray(new Curso[0]));
        JTextField añoField = new JTextField("2024", 10);
        JTextField semestreField = new JTextField("1", 10);

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Profesor:"), gbc);
        gbc.gridx = 1; dialog.add(comboPersonas, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Curso:"), gbc);
        gbc.gridx = 1; dialog.add(comboCursos, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Año:"), gbc);
        gbc.gridx = 1; dialog.add(añoField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Semestre:"), gbc);
        gbc.gridx = 1; dialog.add(semestreField, gbc);

        JPanel botones = new JPanel();
        JButton guardar = new JButton("Guardar");
        JButton cancelar = new JButton("Cancelar");

        guardar.addActionListener(e -> {
            try {
                Persona seleccionada = (Persona) comboPersonas.getSelectedItem();
                Curso curso = (Curso) comboCursos.getSelectedItem();
                int año = Integer.parseInt(añoField.getText().trim());
                int semestre = Integer.parseInt(semestreField.getText().trim());

                Profesor profesor = (seleccionada instanceof Profesor)
                        ? (Profesor) seleccionada
                        : new Profesor(seleccionada.getID(), seleccionada.getNombres(),
                        seleccionada.getApellidos(), seleccionada.getEmail(), "Tiempo Completo");

                CursoProfesor cp = new CursoProfesor(profesor, año, semestre, curso);
                cursosProfesores.agregar(cp);

                JOptionPane.showMessageDialog(dialog, "✅ Curso asignado con éxito.");
                appendOutput("Asignación: " + cp.toString());
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Año y semestre deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(e -> dialog.dispose());

        botones.add(guardar);
        botones.add(cancelar);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(botones, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostrarListados() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PERSONAS ===\n");
        personas.imprimirListado().forEach(p -> sb.append(p).append("\n"));

        sb.append("\n=== CURSOS A PROFESORES ===\n");
        cursosProfesores.imprimirListado().forEach(p -> sb.append(p).append("\n"));

        sb.append("\n=== INSCRIPCIONES DE ESTUDIANTES ===\n");
        cursosInscritos.imprimirListado().forEach(p -> sb.append(p).append("\n"));

        areaSalida.setText(sb.toString());
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas salir?\nSe cerrarán las conexiones a la base de datos.",
                "Confirmar salida", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            personas.cerrarConexion();
            cursosProfesores.cerrarConexion();
            cursosInscritos.cerrarConexion();
            System.exit(0);
        }
    }

    private void appendOutput(String text) {
        areaSalida.append(text + "\n");
    }
}