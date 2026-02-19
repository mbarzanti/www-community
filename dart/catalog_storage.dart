import 'package:dkc_cabinet_configurator/persistence/storage/catalog_storage/catalog_storage_drift_impl.dart';

/// Интерфейс для работы с таблицами соответствия оборудования DKC.
abstract class ICatalogStorage {
  /// Получнение размеров шкафа.
  Stream<List<CabinetSize>> watchCabinetSizes({int? height, int? width, int? depth});
}
