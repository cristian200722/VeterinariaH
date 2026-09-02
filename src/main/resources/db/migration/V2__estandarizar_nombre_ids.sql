-- ============================================
-- MIGRACIÓN V2
-- ESTANDARIZACIÓN DE CLAVES PRIMARIAS Y FORÁNEAS
-- ============================================


-- ============================================
-- CLIENTE
-- ============================================

ALTER TABLE cliente
RENAME COLUMN id_cliente TO id;


-- ============================================
-- MASCOTA
-- ============================================

ALTER TABLE mascota
RENAME COLUMN id_mascota TO id;

ALTER TABLE mascota
RENAME COLUMN id_cliente TO cliente_id;


-- ============================================
-- SERVICIO
-- ============================================

ALTER TABLE servicio
RENAME COLUMN id_servicio TO id;


-- ============================================
-- PAQUETE
-- ============================================

ALTER TABLE paquete
RENAME COLUMN id_paquete TO id;


-- ============================================
-- PRODUCTO
-- ============================================

ALTER TABLE producto
RENAME COLUMN id_producto TO id;


-- ============================================
-- TARIFA_SERVICIO
-- ============================================

ALTER TABLE tarifa_servicio
RENAME COLUMN id_tarifa TO id;

ALTER TABLE tarifa_servicio
RENAME COLUMN id_servicio TO servicio_id;


-- ============================================
-- PAQUETE_SERVICIO
-- ============================================

ALTER TABLE paquete_servicio
RENAME COLUMN id_paquete_servicio TO id;

ALTER TABLE paquete_servicio
RENAME COLUMN id_paquete TO paquete_id;

ALTER TABLE paquete_servicio
RENAME COLUMN id_servicio TO servicio_id;


-- ============================================
-- PRODUCTO_SERVICIO
-- ============================================

ALTER TABLE producto_servicio
RENAME COLUMN id_producto_servicio TO id;

ALTER TABLE producto_servicio
RENAME COLUMN id_servicio TO servicio_id;

ALTER TABLE producto_servicio
RENAME COLUMN id_producto TO producto_id;


-- ============================================
-- VENTA
-- ============================================

ALTER TABLE venta
RENAME COLUMN id_venta TO id;

ALTER TABLE venta
RENAME COLUMN id_cliente TO cliente_id;

ALTER TABLE venta
RENAME COLUMN id_mascota TO mascota_id;


-- ============================================
-- DETALLE_VENTA_SERVICIO
-- ============================================

ALTER TABLE detalle_venta_servicio
RENAME COLUMN id_detalle TO id;

ALTER TABLE detalle_venta_servicio
RENAME COLUMN id_venta TO venta_id;

ALTER TABLE detalle_venta_servicio
RENAME COLUMN id_servicio TO servicio_id;


-- ============================================
-- DETALLE_VENTA_PAQUETE
-- ============================================

ALTER TABLE detalle_venta_paquete
RENAME COLUMN id_detalle TO id;

ALTER TABLE detalle_venta_paquete
RENAME COLUMN id_venta TO venta_id;

ALTER TABLE detalle_venta_paquete
RENAME COLUMN id_paquete TO paquete_id;


-- ============================================
-- CONSUMO_PRODUCTO
-- ============================================

ALTER TABLE consumo_producto
RENAME COLUMN id_consumo TO id;

ALTER TABLE consumo_producto
RENAME COLUMN id_producto TO producto_id;

ALTER TABLE consumo_producto
RENAME COLUMN id_venta TO venta_id;