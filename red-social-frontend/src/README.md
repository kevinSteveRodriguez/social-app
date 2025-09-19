# Estructura del Proyecto - Red Social Frontend

## 📁 Estructura de Carpetas

```
src/
├── components/           # Componentes de React
│   ├── auth/            # Componentes de autenticación
│   │   ├── Login.js     # Componente de login
│   │   └── index.js     # Exportaciones de auth
│   ├── layout/          # Componentes de layout
│   │   ├── Layout.js    # Layout principal
│   │   ├── AuthLayout.js # Layout para autenticación
│   │   └── index.js     # Exportaciones de layout
│   ├── ui/              # Componentes de interfaz reutilizables
│   │   ├── Button.js    # Componente de botón
│   │   ├── Input.js     # Componente de input
│   │   └── index.js     # Exportaciones de UI
│   └── index.js         # Exportaciones principales
├── hooks/               # Hooks personalizados
│   ├── useForm.js       # Hook para manejo de formularios
│   └── index.js         # Exportaciones de hooks
├── utils/               # Utilidades y helpers
│   ├── validation.js    # Funciones de validación
│   └── index.js         # Exportaciones de utilidades
├── constants/           # Constantes de la aplicación
│   └── index.js         # Configuraciones y constantes
├── services/            # Servicios para API
│   ├── authService.js   # Servicio de autenticación
│   └── index.js         # Exportaciones de servicios
├── App.js              # Componente principal
├── App.css             # Estilos globales
└── index.js            # Punto de entrada
```

## 🎨 Tecnologías Utilizadas

- **React 19.1.1** - Framework principal
- **Tailwind CSS** - Framework de estilos
- **PostCSS** - Procesador de CSS
- **Autoprefixer** - Prefijos CSS automáticos

## 🚀 Características Implementadas

### Componentes UI
- **Button**: Botón reutilizable con variantes (primary, secondary, outline, ghost, danger)
- **Input**: Campo de entrada con validación y estados de error

### Layouts
- **Layout**: Layout base para la aplicación
- **AuthLayout**: Layout específico para páginas de autenticación

### Hooks Personalizados
- **useForm**: Hook para manejo de formularios con validación

### Servicios
- **authService**: Servicio para manejo de autenticación (simulado)

### Utilidades
- **validation.js**: Funciones de validación reutilizables
- **index.js**: Utilidades generales (formateo de fechas, texto, etc.)

## 📝 Convenciones de Código

### Nomenclatura
- Componentes: PascalCase (ej: `Login.js`)
- Hooks: camelCase con prefijo "use" (ej: `useForm.js`)
- Servicios: camelCase con sufijo "Service" (ej: `authService.js`)
- Utilidades: camelCase (ej: `validation.js`)

### Estructura de Componentes
```javascript
// Importaciones
import React from 'react';
import { ComponenteHijo } from './ComponenteHijo';

// Componente principal
const MiComponente = ({ prop1, prop2 }) => {
  // Estados y hooks
  const [estado, setEstado] = useState();
  
  // Funciones
  const handleClick = () => {
    // lógica
  };
  
  // Render
  return (
    <div>
      {/* JSX */}
    </div>
  );
};

// Exportación
export default MiComponente;
```

## 🎯 Próximos Pasos

1. **Agregar más componentes UI**: Modal, Card, Badge, etc.
2. **Implementar React Router**: Para navegación entre páginas
3. **Agregar más páginas**: Dashboard, Perfil, Configuración
4. **Integrar con backend real**: Reemplazar servicios simulados
5. **Agregar tests**: Jest y React Testing Library
6. **Implementar estado global**: Context API o Redux

## 🔧 Scripts Disponibles

```bash
npm start          # Iniciar servidor de desarrollo
npm run build      # Construir para producción
npm test           # Ejecutar tests
npm run eject      # Ejectar configuración (no recomendado)
```
