import 'package:dkc_cabinet_configurator/features/settings/domain/entity/master_key.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/master_key_dto_storage.dart';

/// Маппер для получения [MasterKey] из [MasterKeyDtoStorage]
MasterKey mapMasterKeyStorage(MasterKeyDtoStorage masterKeyDtoStorage) {
  return MasterKey(
    value: masterKeyDtoStorage.value,
  );
}
