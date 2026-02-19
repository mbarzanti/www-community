// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'catalog_storage_drift_impl.dart';

// **************************************************************************
// MoorGenerator
// **************************************************************************

// ignore_for_file: type=lint
class CabinetSize extends DataClass implements Insertable<CabinetSize> {
  /// Высота
  final int height;

  /// Ширина
  final int width;

  /// Глубина
  final int depth;
  CabinetSize({required this.height, required this.width, required this.depth});
  factory CabinetSize.fromData(Map<String, dynamic> data, {String? prefix}) {
    final effectivePrefix = prefix ?? '';
    return CabinetSize(
      height: const IntType()
          .mapFromDatabaseResponse(data['${effectivePrefix}height'])!,
      width: const IntType()
          .mapFromDatabaseResponse(data['${effectivePrefix}width'])!,
      depth: const IntType()
          .mapFromDatabaseResponse(data['${effectivePrefix}depth'])!,
    );
  }
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['height'] = Variable<int>(height);
    map['width'] = Variable<int>(width);
    map['depth'] = Variable<int>(depth);
    return map;
  }

  CabinetSizesCompanion toCompanion(bool nullToAbsent) {
    return CabinetSizesCompanion(
      height: Value(height),
      width: Value(width),
      depth: Value(depth),
    );
  }

  factory CabinetSize.fromJson(Map<String, dynamic> json,
      {ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return CabinetSize(
      height: serializer.fromJson<int>(json['height']),
      width: serializer.fromJson<int>(json['width']),
      depth: serializer.fromJson<int>(json['depth']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'height': serializer.toJson<int>(height),
      'width': serializer.toJson<int>(width),
      'depth': serializer.toJson<int>(depth),
    };
  }

  CabinetSize copyWith({int? height, int? width, int? depth}) => CabinetSize(
        height: height ?? this.height,
        width: width ?? this.width,
        depth: depth ?? this.depth,
      );
  @override
  String toString() {
    return (StringBuffer('CabinetSize(')
          ..write('height: $height, ')
          ..write('width: $width, ')
          ..write('depth: $depth')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(height, width, depth);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is CabinetSize &&
          other.height == this.height &&
          other.width == this.width &&
          other.depth == this.depth);
}

class CabinetSizesCompanion extends UpdateCompanion<CabinetSize> {
  final Value<int> height;
  final Value<int> width;
  final Value<int> depth;
  const CabinetSizesCompanion({
    this.height = const Value.absent(),
    this.width = const Value.absent(),
    this.depth = const Value.absent(),
  });
  CabinetSizesCompanion.insert({
    required int height,
    required int width,
    required int depth,
  })  : height = Value(height),
        width = Value(width),
        depth = Value(depth);
  static Insertable<CabinetSize> custom({
    Expression<int>? height,
    Expression<int>? width,
    Expression<int>? depth,
  }) {
    return RawValuesInsertable({
      if (height != null) 'height': height,
      if (width != null) 'width': width,
      if (depth != null) 'depth': depth,
    });
  }

  CabinetSizesCompanion copyWith(
      {Value<int>? height, Value<int>? width, Value<int>? depth}) {
    return CabinetSizesCompanion(
      height: height ?? this.height,
      width: width ?? this.width,
      depth: depth ?? this.depth,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (height.present) {
      map['height'] = Variable<int>(height.value);
    }
    if (width.present) {
      map['width'] = Variable<int>(width.value);
    }
    if (depth.present) {
      map['depth'] = Variable<int>(depth.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('CabinetSizesCompanion(')
          ..write('height: $height, ')
          ..write('width: $width, ')
          ..write('depth: $depth')
          ..write(')'))
        .toString();
  }
}

class $CabinetSizesTable extends CabinetSizes
    with TableInfo<$CabinetSizesTable, CabinetSize> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $CabinetSizesTable(this.attachedDatabase, [this._alias]);
  final VerificationMeta _heightMeta = const VerificationMeta('height');
  @override
  late final GeneratedColumn<int?> height = GeneratedColumn<int?>(
      'height', aliasedName, false,
      type: const IntType(), requiredDuringInsert: true);
  final VerificationMeta _widthMeta = const VerificationMeta('width');
  @override
  late final GeneratedColumn<int?> width = GeneratedColumn<int?>(
      'width', aliasedName, false,
      type: const IntType(), requiredDuringInsert: true);
  final VerificationMeta _depthMeta = const VerificationMeta('depth');
  @override
  late final GeneratedColumn<int?> depth = GeneratedColumn<int?>(
      'depth', aliasedName, false,
      type: const IntType(), requiredDuringInsert: true);
  @override
  List<GeneratedColumn> get $columns => [height, width, depth];
  @override
  String get aliasedName => _alias ?? 'cabinet_sizes';
  @override
  String get actualTableName => 'cabinet_sizes';
  @override
  VerificationContext validateIntegrity(Insertable<CabinetSize> instance,
      {bool isInserting = false}) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('height')) {
      context.handle(_heightMeta,
          height.isAcceptableOrUnknown(data['height']!, _heightMeta));
    } else if (isInserting) {
      context.missing(_heightMeta);
    }
    if (data.containsKey('width')) {
      context.handle(
          _widthMeta, width.isAcceptableOrUnknown(data['width']!, _widthMeta));
    } else if (isInserting) {
      context.missing(_widthMeta);
    }
    if (data.containsKey('depth')) {
      context.handle(
          _depthMeta, depth.isAcceptableOrUnknown(data['depth']!, _depthMeta));
    } else if (isInserting) {
      context.missing(_depthMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => <GeneratedColumn>{};
  @override
  CabinetSize map(Map<String, dynamic> data, {String? tablePrefix}) {
    return CabinetSize.fromData(data,
        prefix: tablePrefix != null ? '$tablePrefix.' : null);
  }

  @override
  $CabinetSizesTable createAlias(String alias) {
    return $CabinetSizesTable(attachedDatabase, alias);
  }
}

abstract class _$CatalogStorageDriftImpl extends GeneratedDatabase {
  _$CatalogStorageDriftImpl(QueryExecutor e)
      : super(SqlTypeSystem.defaultInstance, e);
  late final $CabinetSizesTable cabinetSizes = $CabinetSizesTable(this);
  @override
  Iterable<TableInfo> get allTables => allSchemaEntities.whereType<TableInfo>();
  @override
  List<DatabaseSchemaEntity> get allSchemaEntities => [cabinetSizes];
}
