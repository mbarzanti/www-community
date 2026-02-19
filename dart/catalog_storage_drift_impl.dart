import 'dart:io';

import 'package:dkc_cabinet_configurator/persistence/storage/catalog_storage/catalog_storage.dart';
import 'package:dkc_cabinet_configurator/persistence/storage/catalog_storage/catalog_storage_dart_tables.dart';
import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

part 'catalog_storage_drift_impl.g.dart';

/// Реализация интерфейса работы с таблицами соответствия [ICatalogStorage] через пакет drift.
@DriftDatabase(tables: tables)
class CatalogStorageDriftImpl extends _$CatalogStorageDriftImpl implements ICatalogStorage {
  @override
  int get schemaVersion => 1;

  /// Конструктор.
  CatalogStorageDriftImpl() : super(_openConnection());

  @override
  Stream<List<CabinetSize>> watchCabinetSizes({int? height, int? width, int? depth}) {
    final query = select(cabinetSizes);

    if (height != null && width != null && depth != null) {
      query
        ..where((tbl) => tbl.height.equals(height))
        ..where((tbl) => tbl.width.equals(width))
        ..where((tbl) => tbl.depth.equals(depth));
    }

    if (width != null && depth != null) {
      query
        ..where((tbl) => tbl.width.equals(width))
        ..where((tbl) => tbl.depth.equals(depth));
    }

    if (height != null && depth != null) {
      query
        ..where((tbl) => tbl.height.equals(height))
        ..where((tbl) => tbl.depth.equals(depth));
    }

    if (height != null && width != null) {
      query
        ..where((tbl) => tbl.height.equals(height))
        ..where((tbl) => tbl.width.equals(width));
    }

    if (height != null) {
      query.where((tbl) => tbl.height.equals(height));
    }

    if (width != null) {
      query.where((tbl) => tbl.width.equals(width));
    }

    if (depth != null) {
      query.where((tbl) => tbl.depth.equals(depth));
    }

    return query.watch();
  }
}

/// Подключение к базе данных.
LazyDatabase _openConnection() {
  return LazyDatabase(() async {
    // Создаем файл базы данных в директории файлов приложения на устройстве.
    final dbFolder = await getApplicationDocumentsDirectory();
    final file = File(p.join(dbFolder.path, 'dkc_db.sqlite'));

    // Если файла в директории не существует, делаем копию базы данных из состава приложения.
    if (!file.existsSync()) {
      final blob = await rootBundle.load('assets/db/dkc_db.sqlite');
      final buffer = blob.buffer;
      await file.writeAsBytes(buffer.asUint8List(blob.offsetInBytes, blob.lengthInBytes));
    }

    return NativeDatabase(file);
  });
}
