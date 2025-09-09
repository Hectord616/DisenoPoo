package com.login.views;

import com.login.Main;
import com.login.model.*;
import com.login.repository.CursosInscritos;
import com.login.repository.CursosProfesores;
import com.login.repository.InscripcionesPersonas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

public class InterfazGrafica extends JFrame {

    private final InscripcionesPersonas personas;
    private final CursosProfesores cursosProfesores;
    private final CursosInscritos cursosInscritos;

    private final List<Curso> listaCursos;
    private final List<Facultad> listaFacultades;
    private final List<Programa> listaProgramas;

    private final JTabbedPane pestañas;
    private final JTable tablaPersonas;
    private final JTable tablaFacultades;
    private final JTable tablaProgramas;
    private final JTable tablaCursosProfesores;
    private final JTable tablaInscripciones;

    public InterfazGrafica(InscripcionesPersonas personas,
                           CursosProfesores cursosProfesores,
                           CursosInscritos cursosInscritos,
                           List<Curso> listaCursos) {
        this.personas = personas;
        this.cursosProfesores = cursosProfesores;
        this.cursosInscritos = cursosInscritos;
        this.listaCursos = listaCursos;

        this.listaFacultades = new ArrayList<>();
        this.listaProgramas = new ArrayList<>();

        setTitle("Sistema Universitario - GUI");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(7, 1, 5, 5));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnFacultades = new JButton("Registrar Facultad");
        JButton btnProgramas = new JButton("Registrar Programa");
        JButton btnPersonas = new JButton("Registrar Persona");
        JButton btnCursos = new JButton("Registrar Curso");
        JButton btnInscribir = new JButton("Inscribir Estudiante a Curso");
        JButton btnAsignar = new JButton("Asignar Curso a Profesor");
        JButton btnListados = new JButton("Mostrar Listados");
        JButton btnSalir = new JButton("Salir");

        panelBotones.add(btnFacultades);
        panelBotones.add(btnProgramas);
        panelBotones.add(btnPersonas);
        panelBotones.add(btnCursos);
        panelBotones.add(btnInscribir);
        panelBotones.add(btnAsignar);
        panelBotones.add(btnListados);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.WEST);

        // Pestañas
        pestañas = new JTabbedPane();
        tablaPersonas = new JTable(new DefaultTableModel(new Object[]{"ID", "Nombres", "Apellidos", "Email"}, 0));
        tablaFacultades = new JTable(new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0));
        tablaProgramas = new JTable(new DefaultTableModel(new Object[]{"ID", "Nombre", "Facultad"}, 0));
        tablaCursosProfesores = new JTable(new DefaultTableModel(new Object[]{"Profesor", "Curso", "Año", "Semestre"}, 0));
        tablaInscripciones = new JTable(new DefaultTableModel(new Object[]{"Estudiante", "Curso", "Año", "Semestre"}, 0));

        pestañas.addTab("Personas", new JScrollPane(tablaPersonas));
        pestañas.addTab("Facultades", new JScrollPane(tablaFacultades));
        pestañas.addTab("Programas", new JScrollPane(tablaProgramas));
        pestañas.addTab("Cursos Profesores", new JScrollPane(tablaCursosProfesores));
        pestañas.addTab("Inscripciones", new JScrollPane(tablaInscripciones));

        add(pestañas, BorderLayout.CENTER);

        // Acciones
        btnFacultades.addActionListener(e -> abrirVentanaRegistrarFacultad());
        btnProgramas.addActionListener(e -> abrirVentanaRegistrarPrograma());
        btnPersonas.addActionListener(e -> abrirVentanaRegistrarPersona());
        btnCursos.addActionListener(e -> abrirVentanaRegistrarCurso());
        btnInscribir.addActionListener(e -> abrirVentanaInscribirEstudiante());
        btnAsignar.addActionListener(e -> abrirVentanaAsignarCursoProfesor());
        btnListados.addActionListener(e -> mostrarListados());
        btnSalir.addActionListener(e -> salir());

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

    private void abrirVentanaRegistrarFacultad() {
        JDialog dialog = new JDialog(this, "Registrar Facultad", true);
        dialog.setLayout(new GridLayout(3, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField nombreField = new JTextField();

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Nombre:"));
        dialog.add(nombreField);

        JPanel panelBoton = new JPanel();
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String nombre = nombreField.getText().trim();
                Facultad f = new Facultad(id, nombre);
                listaFacultades.add(f);
                JOptionPane.showMessageDialog(dialog, "Facultad registrada con éxito.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBoton.add(guardar);
        dialog.add(panelBoton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void abrirVentanaRegistrarPrograma() {
        if (listaFacultades.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe registrar al menos una Facultad.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Registrar Programa", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField nombreField = new JTextField();
        JComboBox<Facultad> comboFacultades = new JComboBox<>(listaFacultades.toArray(new Facultad[0]));

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Nombre:"));
        dialog.add(nombreField);
        dialog.add(new JLabel("Facultad:"));
        dialog.add(comboFacultades);

        JPanel panelBoton = new JPanel();
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String nombre = nombreField.getText().trim();
                Facultad f = (Facultad) comboFacultades.getSelectedItem();
                Programa p = new Programa(id, nombre, f);
                listaProgramas.add(p);
                JOptionPane.showMessageDialog(dialog, "Programa registrado con éxito.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBoton.add(guardar);
        dialog.add(panelBoton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void abrirVentanaRegistrarPersona() {
        JDialog dialog = new JDialog(this, "Registrar Persona", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField nombreField = new JTextField();
        JTextField apellidoField = new JTextField();
        JTextField emailField = new JTextField();

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Nombres:"));
        dialog.add(nombreField);
        dialog.add(new JLabel("Apellidos:"));
        dialog.add(apellidoField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);

        JPanel panelBoton = new JPanel();
        JButton guardar = new JButton("Guardar");
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

                JOptionPane.showMessageDialog(dialog, "Persona registrada con éxito.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBoton.add(guardar);
        dialog.add(panelBoton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void abrirVentanaRegistrarCurso() {
        if (listaProgramas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe registrar al menos un Programa.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Registrar Curso", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField nombreField = new JTextField();
        JComboBox<Programa> comboProgramas = new JComboBox<>(listaProgramas.toArray(new Programa[0]));

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Nombre:"));
        dialog.add(nombreField);
        dialog.add(new JLabel("Programa:"));
        dialog.add(comboProgramas);

        JPanel panelBoton = new JPanel();
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String nombre = nombreField.getText().trim();
                Programa programa = (Programa) comboProgramas.getSelectedItem();
                Curso curso = new Curso(id, nombre, programa, true);
                listaCursos.add(curso);
                JOptionPane.showMessageDialog(dialog, "Curso registrado con éxito.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBoton.add(guardar);
        dialog.add(panelBoton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void abrirVentanaInscribirEstudiante() {
        List<Persona> listaPersonas = personas.getListado();
        if (listaPersonas.isEmpty() || listaCursos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay personas o cursos disponibles.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Inscribir Estudiante", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));

        JComboBox<Persona> comboPersonas = new JComboBox<>(listaPersonas.toArray(new Persona[0]));
        JComboBox<Curso> comboCursos = new JComboBox<>(listaCursos.toArray(new Curso[0]));
        JTextField añoField = new JTextField("2024");
        JTextField semestreField = new JTextField("1");

        dialog.add(new JLabel("Estudiante:"));
        dialog.add(comboPersonas);
        dialog.add(new JLabel("Curso:"));
        dialog.add(comboCursos);
        dialog.add(new JLabel("Año:"));
        dialog.add(añoField);
        dialog.add(new JLabel("Semestre:"));
        dialog.add(semestreField);

        JPanel panelBoton = new JPanel();
        JButton guardar = new JButton("Guardar");
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

                JOptionPane.showMessageDialog(dialog, "Estudiante inscrito con éxito.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Año y semestre deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBoton.add(guardar);
        dialog.add(panelBoton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void abrirVentanaAsignarCursoProfesor() {
        List<Persona> listaPersonas = personas.getListado();
        if (listaPersonas.isEmpty() || listaCursos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay personas o cursos disponibles.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Asignar Curso a Profesor", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));

        JComboBox<Persona> comboPersonas = new JComboBox<>(listaPersonas.toArray(new Persona[0]));
        JComboBox<Curso> comboCursos = new JComboBox<>(listaCursos.toArray(new Curso[0]));
        JTextField añoField = new JTextField("2024");
        JTextField semestreField = new JTextField("1");

        dialog.add(new JLabel("Profesor:"));
        dialog.add(comboPersonas);
        dialog.add(new JLabel("Curso:"));
        dialog.add(comboCursos);
        dialog.add(new JLabel("Año:"));
        dialog.add(añoField);
        dialog.add(new JLabel("Semestre:"));
        dialog.add(semestreField);

        JPanel panelBoton = new JPanel();
        JButton guardar = new JButton("Guardar");
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

                JOptionPane.showMessageDialog(dialog, "Curso asignado con éxito.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Año y semestre deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBoton.add(guardar);
        dialog.add(panelBoton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostrarListados() {
        DefaultTableModel modelPersonas = (DefaultTableModel) tablaPersonas.getModel();
        modelPersonas.setRowCount(0);
        for (Persona p : personas.getListado()) {
            modelPersonas.addRow(new Object[]{p.getID(), p.getNombres(), p.getApellidos(), p.getEmail()});
        }

        DefaultTableModel modelFacultades = (DefaultTableModel) tablaFacultades.getModel();
        modelFacultades.setRowCount(0);
        for (Facultad f : listaFacultades) {
            modelFacultades.addRow(new Object[]{f.getID(), f.getNombre()});
        }

        DefaultTableModel modelProgramas = (DefaultTableModel) tablaProgramas.getModel();
        modelProgramas.setRowCount(0);
        for (Programa p : listaProgramas) {
            modelProgramas.addRow(new Object[]{p.getID(), p.getNombre(), p.getFacultad().getNombre()});
        }

        DefaultTableModel modelCursosProfes = (DefaultTableModel) tablaCursosProfesores.getModel();
        modelCursosProfes.setRowCount(0);
        for (CursoProfesor cp : cursosProfesores.imprimirListado()) {
            modelCursosProfes.addRow(new Object[]{
                    cp.getProfesor().getNombres() + " " + cp.getProfesor().getApellidos(),
                    cp.getCurso().getNombre(),
                    cp.getAnio(),
                    cp.getSemestre()
            });
        }

        DefaultTableModel modelInscripciones = (DefaultTableModel) tablaInscripciones.getModel();
        modelInscripciones.setRowCount(0);
        for (Inscripcion ins : cursosInscritos.imprimirListado()) {
            modelInscripciones.addRow(new Object[]{
                    ins.getEstudiante().getNombres() + " " + ins.getEstudiante().getApellidos(),
                    ins.getCurso().getNombre(),
                    ins.getAño(),
                    ins.getSemestre()
            });
        }
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
}
