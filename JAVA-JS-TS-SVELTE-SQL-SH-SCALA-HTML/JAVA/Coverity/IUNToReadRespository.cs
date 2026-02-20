#nullable enable
using CommonTools;
using PagoPaSendCommon.DAL.Models;
using PagoPaSendCommon.DAL.Models.CRUD;
using PagoPaSendCommon.DAL.Utils;
using System.Data.Common;
using System.Data;
using System.Linq;

namespace PagoPaSendCommon.DAL.Repositories
{
    public class IUNToReadRepository
    {
        public (IdNexiFile, IUN[])[] GetIUNAndIdNexiFileOfRowsOfIUNToProcess(IProxyChannel ch)
        {
            (IdNexiFile, IUN[])[] result = default!;
            void action(DbDataReader rdr) => result = cDbDataProxy2
                .Enumerate<IUNAndIdNexiFile>(rdr)
                .GroupBy(v => v.IdNexiFile)
                .Select(v => (v.Key, v.Select(g => new IUN(g.IUN!)).ToArray()))
                .ToArray();

            cDbDataProxy.ExecuteQuery_Reader(action,
                "SELECT NF.IDNEXIFILE, NF.IUN FROM EPP_NEXI_FILE_ROW NF INNER JOIN EPP_NEXI_IUN_TO_READ NITR ON NF.IUN = NITR.IUN WHERE NITR.IS_DELETED = 0",
                ch);
            return result;
        }

        public IUN[] GetAllNotDeleted(IProxyChannel ch)
        {
            IUN[] result = default!;
            void action(DbDataReader rdr) => result = cDbDataProxy2.Enumerate<NexiIUNToRead>(rdr).Select(v => v.IUN).ToArray();

            cDbDataProxy.ExecuteQuery_Reader(action,
                "SELECT IUN FROM EPP_NEXI_IUN_TO_READ WHERE IS_DELETED = 0",
                ch);

            return result;
        }

        public bool Exists(IProxyChannel ch, IUN iun)
        {
            var pList = new[]
             {
                ch.NewParameter("IUN", (string)iun, DbType.String)
            };
            bool result = false;
            void action(DbDataReader rdr) => result = cDbDataProxy2.Enumerate<NexiIUNToRead>(rdr).Any();

            cDbDataProxy.ExecuteQuery_Reader(action,
                "SELECT IUN FROM EPP_NEXI_IUN_TO_READ WHERE IUN = :IUN",
                ch, pList);

            return result;
        }

        public void Insert(IProxyChannel ch, IUN iun)
        {
            var pList = new[]
            {
                ch.NewParameter("IUN", (string)iun, DbType.String)
            };

            string sql = @"INSERT INTO EPP_NEXI_IUN_TO_READ(IUN) VALUES(:IUN)";

            cDbDataProxy.ExecuteQuery_NonQuery(sql, ch, pList);
        }

        public void UnDelete(IProxyChannel ch, IUN iun)
        {
            var pList = new[]
            {
                ch.NewParameter("IUN", (string)iun, DbType.String)
            };

            string sql = @"UPDATE EPP_NEXI_IUN_TO_READ SET IS_DELETED=0, UPDATE_DATE = CURRENT_TIMESTAMP, DELETE_DATE = NULL WHERE IUN = :IUN";

            cDbDataProxy.ExecuteQuery_NonQuery(sql, ch, pList);
        }

        public void Delete(IProxyChannel ch, IUN iun)
        {
            var pList = new[]
            {
                ch.NewParameter("IUN", (string)iun, DbType.String)
            };

            string sql = @"UPDATE EPP_NEXI_IUN_TO_READ SET IS_DELETED=1, DELETE_DATE = CURRENT_TIMESTAMP WHERE IUN = :IUN";

            cDbDataProxy.ExecuteQuery_NonQuery(sql, ch, pList);
        }
    }
}
