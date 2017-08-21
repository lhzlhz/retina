"""LCM type definitions
This file automatically generated by lcm.
DO NOT MODIFY BY HAND!!!!
"""

try:
    import cStringIO.StringIO as BytesIO
except ImportError:
    from io import BytesIO
import struct

class BinaryBlob(object):
    __slots__ = ["data_length", "data"]

    def __init__(self):
        self.data_length = 0
        self.data = []

    def encode(self):
        buf = BytesIO()
        buf.write(BinaryBlob._get_packed_fingerprint())
        self._encode_one(buf)
        return buf.getvalue()

    def _encode_one(self, buf):
        buf.write(struct.pack(">i", self.data_length))
        buf.write(struct.pack('>%db' % self.data_length, *self.data[:self.data_length]))

    def decode(data):
        if hasattr(data, 'read'):
            buf = data
        else:
            buf = BytesIO(data)
        if buf.read(8) != BinaryBlob._get_packed_fingerprint():
            raise ValueError("Decode error")
        return BinaryBlob._decode_one(buf)
    decode = staticmethod(decode)

    def _decode_one(buf):
        self = BinaryBlob()
        self.data_length = struct.unpack(">i", buf.read(4))[0]
        self.data = struct.unpack('>%db' % self.data_length, buf.read(self.data_length))
        return self
    _decode_one = staticmethod(_decode_one)

    _hash = None
    def _get_hash_recursive(parents):
        if BinaryBlob in parents: return 0
        tmphash = (0x9c7079c442ed5c7c) & 0xffffffffffffffff
        tmphash  = (((tmphash<<1)&0xffffffffffffffff)  + (tmphash>>63)) & 0xffffffffffffffff
        return tmphash
    _get_hash_recursive = staticmethod(_get_hash_recursive)
    _packed_fingerprint = None

    def _get_packed_fingerprint():
        if BinaryBlob._packed_fingerprint is None:
            BinaryBlob._packed_fingerprint = struct.pack(">Q", BinaryBlob._get_hash_recursive([]))
        return BinaryBlob._packed_fingerprint
    _get_packed_fingerprint = staticmethod(_get_packed_fingerprint)

